/*
 * Copyright 2013-2014 the original author or authors.
 */

package com.lakala.ips.router.szt;

/**
 *  TODO 类描述
 * <p>
 * Created on 2016年11月15日
 * <p>
 * @author Administrator
 * @since 2016年11月15日
 */

import com.lakala.amber.AmberRuntimeException;
import com.lakala.amber.channel.CommunicationException;
import com.lakala.amber.channel.PayloadResolver;
import com.lakala.amber.channel.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenjian on 16/11/3.
 */
public class ManualSocketGateway implements GatewayExtend {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String host;

    private int port;

    private int connectTimeout;

    private boolean reuseAddress;

    private int receiveBufferSize;
    private int sendBufferSize;
    private int soLinger = -1;
    private int soTimeout;

    private PayloadResolver<byte[]> streamResolver;

    private File dumpPath;

//    private Socket socket;

    private AtomicInteger dumpIndex = new AtomicInteger();

    private Map<String, Socket> socketMap = new HashMap<String, Socket>();

    public void setDumpIndex(AtomicInteger dumpIndex) {
        this.dumpIndex = dumpIndex;
    }

    public void setDumpPath(File dumpPath) {
        this.dumpPath = dumpPath;
        if (!dumpPath.exists())
            dumpPath.mkdirs();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public void setPayloadResolver(PayloadResolver<byte[]> streamResolver) {
        this.streamResolver = streamResolver;
    }

    public Object receive(Object hint, String id) throws CommunicationException {
        throw new UnsupportedOperationException();
    }

    private void dump(byte[] data, String id, int index, boolean response) {
        StringBuffer names = new StringBuffer();
        if (id != null && id.length() > 0)
            names.append(id).append('#');
        if (response) {
            names.append(index).append(".rcv");
        } else {
            names.append(index).append(".snd");
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(dumpPath, names.toString()));
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                }
        }
    }

    public Object send(Object packet, String id) throws CommunicationException {
        byte[] bytes = (byte[]) packet;
        if (dumpPath != null) {
            dump(bytes, id, dumpIndex.getAndIncrement(), false);
        }

        Socket socket = newSocket();
        try {
            socket.getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new CommunicationException(false, "write socket error", e);
        } finally {
            closeSocket(socket);
        }
        return null;
    }

    public Object sendAndReceive(Object request, String id) throws CommunicationException {
        if (streamResolver == null)
            throw new IllegalArgumentException("no stream resolver defined");

        byte[] bytes = (byte[]) request;

        int index = 0;
        if (dumpPath != null) {
            index = dumpIndex.getAndIncrement();
            dump(bytes, id, index, false);
        }

        Socket socket = newSocket(id);
        try {
            socket.getOutputStream().write(bytes);
            byte[] ret = streamResolver.resolve(new Request(socket, socket.getInputStream()));
            if (dumpPath != null) {
                dump(ret, id, index, true);
            }

            return ret;
        } catch (IOException e) {
            closeSocket(socket, id);
            throw new CommunicationException(false, "write socket error", e);
        } catch (AmberRuntimeException e) {
            closeSocket(socket, id);
            Throwable c = e.getCause();
            if (c instanceof SocketTimeoutException){
                throw new CommunicationException(true, "read socket error", e);
            }
            throw new CommunicationException(false, "read socket error", e);
        }
    }

    protected void config(Socket socket) throws SocketException {
        if (receiveBufferSize > 0)
            socket.setReceiveBufferSize(receiveBufferSize);
        if (sendBufferSize > 0)
            socket.setSendBufferSize(sendBufferSize);
        if (soLinger >= 0)
            socket.setSoLinger(true, soLinger);
        if (soTimeout > 0)
            socket.setSoTimeout(soTimeout);
    }

    protected Socket newSocket(String id){
        Socket socket = socketMap.get(id);
        if(socket == null){
            socket = newSocket();
            socketMap.put(id, socket);
        }else{
            if(socket.isClosed()){
                throw new AmberRuntimeException("socket is close");
            }
        }
        return socket;

    }

    protected Socket newSocket() {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setReuseAddress(this.reuseAddress);
            config(socket);

            InetSocketAddress address = new InetSocketAddress(host, port);
            if (this.connectTimeout > 0)
                socket.connect(address, this.connectTimeout);
            else
                socket.connect(address);
            return socket;
        } catch (IOException e) {
            if (socket != null)
                closeSocket(socket);
            throw new AmberRuntimeException("cannot_new_socket", e);
        }
    }

    private void closeSocket(Socket socket, String id) {
        closeSocket(socket);
        socketMap.remove(id);
    }

    private void closeSocket(Socket socket){
        try {
            System.out.println("关闭socket。。。。。。");
            if(socket == null){
                return ;
            }
            socket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void closesocket(String id) throws CommunicationException {
        Socket socket = socketMap.get(id);
        closeSocket(socket, id);
    }
}