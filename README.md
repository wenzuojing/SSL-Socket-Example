# SSL-Socket-Example

## 生成证书

#### 创建服务端keystore
```
keytool -genkey -keystore server.jks -storepass 123456 -keyalg RSA -keypass 123456
```

#### 导出服务端证书
```
keytool -export -keystore server.jks -storepass 123456 -file server.cer
```

#### 将服务端证书导入到客户端trustkeystroe

```
keytool -import -keystore clientTrust.jks -storepass 123456 -file server.cer
```

