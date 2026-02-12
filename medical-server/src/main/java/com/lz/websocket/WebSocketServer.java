package com.lz.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam; // 🔥 引入路径参数注解
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

// 🔥 修改路径，带上 userId 参数
// 例如前端连接：ws://localhost:8080/ws/audit/1001 (1001是医生ID)
@ServerEndpoint("/ws/audit/{userId}")
@Component
public class WebSocketServer {

    // 🔥 使用 Map 替代 Set，Key 是 userId，Value 是连接对象
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    private Session session;
    private String userId; // 当前连接的用户ID

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;

        // 将当前用户存入 Map
        webSocketMap.put(userId, this);

        System.out.println("【WebSocket】用户 " + userId + " 已连接，当前在线人数:" + webSocketMap.size());
    }

    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            System.out.println("【WebSocket】用户 " + userId + " 断开连接");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("【WebSocket】用户 " + userId + " 发生错误");
        error.printStackTrace();
    }

    // ==========================================
    // 🔥 核心修改：点对点发送方法
    // ==========================================
    public static void sendToUser(String targetUserId, String message) {
        if (targetUserId != null && webSocketMap.containsKey(targetUserId)) {
            try {
                webSocketMap.get(targetUserId).sendMessage(message);
                System.out.println("【WebSocket】消息已发送给用户 " + targetUserId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("【WebSocket】用户 " + targetUserId + " 不在线，消息发送失败");
            // 这里可以考虑将消息存入数据库，等用户上线后再拉取（离线消息逻辑）
        }
    }

    /**
     * 群发（保留备用，例如系统公告）
     */
    public static void sendToAll(String message) {
        for (String key : webSocketMap.keySet()) {
            try {
                webSocketMap.get(key).sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}