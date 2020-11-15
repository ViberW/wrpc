package com.wyber.rpc.registry.zookeeper;

import com.wyber.rpc.common.tools.URIUtils;
import com.wyber.rpc.registry.NotifyListener;
import com.wyber.rpc.registry.RegistryService;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 16:44
 */
public class ZookeeperRegistry implements RegistryService, Watcher {

    private static String ROOT_PATH = "/wrpc";
    private ZooKeeper zooKeeper;
    private volatile boolean init = false;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    Map<String, Set<URI>> localCache = new ConcurrentHashMap<>();
    Map<String, NotifyListener> listenerMap = new ConcurrentHashMap<>();
    Map<String, Watcher> watchers = new ConcurrentHashMap<>();//保存需要监听的watcher

    @Override
    public void process(WatchedEvent event) {
        if (!this.init && event.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("zookeeper connect ok");
            countDownLatch.countDown();
            init = true;
        }
    }

    @Override
    public void register(URI uri) {
        checkInit();
        String path = buildServerPath(uri);
        // 创建`/wrpc/{serviceName}/{side}`  --持久化
        cycleCreateNode(path);
        try {
            // `/wrpc/{serviceName}/{side}/{uri}`为临时节点
            path += ("/" + URLEncoder.encode(uri.toString(), "utf-8"));
            System.out.println("准备向外暴露的url:" + path);
            Stat stat = this.zooKeeper.exists(path, null);
            if (stat == null) {
                this.zooKeeper.create(path, null,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("register within zookeeper interrupted", e);
        }
    }

    @Override
    public void subscribe(String service, NotifyListener notifyListener) {
        try {
            if (localCache.get(service) == null) {
                localCache.putIfAbsent(service, new HashSet<>());
                listenerMap.putIfAbsent(service, notifyListener);

                String providerPath = buildServerPath("/" + service, "provider");
                //获取exportUrl
                Watcher watcher = watchers.computeIfAbsent(service, s -> new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        try {
                            if (event.getState() == Event.KeeperState.SyncConnected
                                    && event.getType() == Event.EventType.NodeChildrenChanged) {
                                List<String> children = ZookeeperRegistry.this.zooKeeper
                                        .getChildren(providerPath, this);
                                getDataAndCheck(service, children);
                            }
                        } catch (KeeperException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                List<String> children = this.zooKeeper.getChildren(providerPath, watcher);
                getDataAndCheck(service, children);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataAndCheck(String service, List<String> children) {
        try {
            if (null == children) {
                children = Collections.emptyList();
            }
            System.out.println("有provider服务变更:" + children);
            Set<URI> uris = children.stream().map(s -> {
                try {
                    return new URI(URLDecoder.decode(s, "utf-8"));
                } catch (Exception e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toSet());
            localCache.put(service, uris);
            //通知
            listenerMap.get(service).notify(uris);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(URI address) {
        try {
            String hostPort = buildZkUrl(address);
            int sessionTimeout = URIUtils.getIntParam(address,
                    "session-timeout", 3000);
            zooKeeper = new ZooKeeper(hostPort, sessionTimeout, this);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("init registry failure within zookeeper", e);
        }
    }

    /**
     * 循环创建节点
     *
     * @param path 节点路径
     */
    private void cycleCreateNode(String path) {
        try {
            String[] paths = path.split("/");
            String pathNode = "";
            for (String p : paths) {
                if (StringUtils.isEmpty(p)) {
                    continue;
                }
                pathNode += ("/" + p);
                try {
                    Stat stat = this.zooKeeper.exists(pathNode, null);
                    if (null == stat) {
                        this.zooKeeper.create(pathNode, null,
                                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                } catch (KeeperException e) {
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("register export-url failure within zookeeper", e);
        }
    }

    private void checkInit() {
        if (!this.init) {
            throw new IllegalStateException("cannot operate registry before init");
        }
    }

    private String buildZkUrl(URI address) {
        StringBuilder zkUrl = new StringBuilder();
        zkUrl.append(address.getHost()).append(":").append(address.getPort());
        return zkUrl.toString();
    }

    private String buildServerPath(URI uri) {
        String serviceName = uri.getPath();
        String side = URIUtils.getParam(uri, "side", "provider");
        return buildServerPath(serviceName, side);
    }

    private String buildServerPath(String serviceName, String side) {
        return new StringBuilder(ROOT_PATH).append(serviceName)
                .append("/").append(side).toString();
    }
}
