package com.baizhitong.util;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

/**
 * 
 * @goal uploadPackage
 * @phase process-sources
 */
public class UploadFile extends AbstractMojo {

    /**
     * �ϴ���ip��ַ
     * 
     * @parameter property="ip"
     */
    private String ip;

    /**
     * ���֮���ϴ���Ŀ��ģ��
     * 
     * @parameter property="module"
     */
    private String module;

    /**
     * ���֮����ļ���
     * @parameter property="project.build.finalName"
     */
    private String finalName;
    
    /**
     * ���֮����ļ�����չ��
     * @parameter property="project.packaging"
     */
    private String packageName;
    
    /**
     * TargetĿ¼��һ��Ϊjar���֮���Ŀ¼
     * 
     * @parameter property="project.build.directory"
     * @required
     */
    private File   outputDirectory;
    
    /**
     * ��ǰjar�汾��
     * @parameter property="project.version"
     */
    private String version;
    
    /**
     * �����������Ŀǰ��֧��http����֧��sockets����
     * @parameter property="socksProxy"
     */
    private String socksProxy;

    public void execute() throws MojoExecutionException, MojoFailureException {
        String filename = outputDirectory + "/" + finalName + "." + packageName;
        url = "https://" + ip + "/upload/doUpload";
        System.err.println("��ʼ�ϴ��ļ�("+ filename+") module:("+module+") ��:----->" + ip);
        String content = doUpload(filename,module);
        System.err.println("�ļ��ϴ����~~~" + content);
    }

    String         url      = "";
    private String password = "123456";

    public String doUpload(String filename,String module) {
        CloseableHttpResponse response = null;
        try {
            // �ͻ���֤�飬�൱�������������֤��Ի���
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(UploadFile.class.getResourceAsStream("/client.p12"), password.toCharArray());

            // ����֤�飬�൱�ڷ��������ĸ�֤��
            KeyStore trustStore = KeyStore.getInstance("pkcs12");// KeyStore.getDefaultType()
            trustStore.load(UploadFile.class.getResourceAsStream("/client.p12"), password.toCharArray());

            // ��ʼ��֤�黷��
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSignedStrategy())
                            .loadKeyMaterial(keyStore, password.toCharArray()).build();
            HttpClientBuilder builder = HttpClientBuilder.create();
            SSLConnectionSocketFactory sslConnectionFactory = new BztSSLConnectionSocketFactory(sslcontext,new BztHostnameVerifier());
            builder.setSSLSocketFactory(sslConnectionFactory);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                            .register("https", sslConnectionFactory)
                            .register("http", new PlainConnectionSocketFactory()).build();
            HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
            builder.setConnectionManager(ccm);

            CloseableHttpClient httpclient = builder.build();
            HttpPost httppost = new HttpPost(url);
            // ���Ͱ汾�ļ�
            FileBody bin = new FileBody(new File(filename));
            StringBody comment = new StringBody(module, ContentType.TEXT_PLAIN);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("upfile", bin)
                            .addPart("versionType", comment).build();
            httppost.setEntity(reqEntity);

            HttpClientContext context = HttpClientContext.create();
            
            //����sockets����
            if(socksProxy!= null && !"".equals(socksProxy))
            {
                String[] strs = socksProxy.split(":");
                InetSocketAddress socksaddr = new InetSocketAddress(strs[0], Integer.parseInt(strs[1]));
                context.setAttribute("socks.address", socksaddr);
            }
            
            // ִ��sql��ѯ
            response = httpclient.execute(httppost,context);
            HttpEntity entity = response.getEntity();

            // entity.writeTo(new FileOutputStream("C:/temp/1111/1.txt"));
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return "�����쳣la~";
        } finally {
            try {
                response.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * ����������У��
     * @author xuaihua
     */
    class BztHostnameVerifier implements HostnameVerifier
    {

        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
        
    }
    
    /**
     * α��֤����֤����
     * 
     * @author xuaihua
     *
     */
    class TrustSignedStrategy implements TrustStrategy {
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }
    }

    class BztSSLConnectionSocketFactory extends SSLConnectionSocketFactory
    {

        public BztSSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
            super(sslContext, hostnameVerifier);
        }
        
        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            //ʹ�ô��������
            if(socksaddr != null)
            {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
                return new Socket(proxy);
            }
            else
            {
                return SocketFactory.getDefault().createSocket();
            }
        }
        
        @Override
        public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host,
                        final InetSocketAddress remoteAddress, final InetSocketAddress localAddress,
                        final HttpContext context) throws IOException, ConnectTimeoutException {
            Socket sock;
            if (socket != null) {
                sock = socket;
            } else {
                sock = createSocket(context);
            }
            if (localAddress != null) {
                sock.bind(localAddress);
            }
            try {
                sock.connect(remoteAddress, connectTimeout);
            } catch (SocketTimeoutException ex) {
                throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
            }
            // Setup SSL layering if necessary
            if (sock instanceof SSLSocket) {
                final SSLSocket sslsock = (SSLSocket) sock;
                sslsock.startHandshake();
                return sock;
            } else {
                return createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
            }
        }
    }
    
    @Test
    public void test() {
        String filename = "f:/nohup.out";
        url = "https://10.31.0.75:7999/upload/doUpload";
        String s = doUpload(filename,"mooc");
        System.err.println(s);
    }
}
