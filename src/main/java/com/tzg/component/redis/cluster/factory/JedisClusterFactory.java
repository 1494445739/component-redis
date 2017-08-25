package com.tzg.component.redis.cluster.factory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class JedisClusterFactory implements FactoryBean< JedisCluster >, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger( JedisClusterFactory.class.getName() );

    private static final String SEPARATOR = ",";

    //集群地址多个地址默认逗号分隔
    private String hostAndPorts;

    //集群对象
    private JedisCluster jedisCluster;

    //连接超时时间
    private Integer timeout;

    //最大重定向次数
    private Integer maxAttempts;

    //redis连接池
    private JedisPoolConfig jedisPoolConfig;

    @Override
    public JedisCluster getObject() throws Exception {
        return jedisCluster;
    }

    @Override
    public Class< ? > getObjectType() {
        return this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if ( StringUtils.isBlank( hostAndPorts ) )
            throw new IllegalArgumentException( "Property 'hostAndPorts' cannot be null" );
        String[] array = StringUtils.split( hostAndPorts, SEPARATOR );

        Set< HostAndPort > jedisClusterNode = new HashSet< HostAndPort >();
        Pattern            pattern          = Pattern.compile( "^.+[:]\\d{1,5}\\s*$" );
        Arrays.stream( array ).forEach( ( item ) -> {
            boolean matche = pattern.matcher( item ).matches();
            if ( !matche ) {
                logger.warn( "Parameters=[{}] of illegal, expect:ip:port", item );
            } else {
                String[] items = item.split( ":" );
                jedisClusterNode.add( new HostAndPort( items[ 0 ], Integer.valueOf( items[ 1 ] ) ) );
            }
        } );
        Predicate< Set< HostAndPort > > predicate = ( set ) -> set.isEmpty();
        if ( predicate.test( jedisClusterNode ) ) {
            throw new IllegalArgumentException( "the value of 'hostAndPorts' is illegal" );
        }

        jedisCluster = new JedisCluster( jedisClusterNode, timeout, maxAttempts, jedisPoolConfig );
        Map< String, JedisPool > nodes = jedisCluster.getClusterNodes();
        nodes.forEach( ( k, v ) -> {
            logger.debug( "key:{}, pool:{}, info:{}", k, v, v.getResource().info() );
        } );

    }

    public String getHostAndPorts() {
        return hostAndPorts;
    }

    public void setHostAndPorts( String hostAndPorts ) {
        this.hostAndPorts = hostAndPorts;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster( JedisCluster jedisCluster ) {
        this.jedisCluster = jedisCluster;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout( Integer timeout ) {
        this.timeout = timeout;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts( Integer maxAttempts ) {
        this.maxAttempts = maxAttempts;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public void setJedisPoolConfig( JedisPoolConfig jedisPoolConfig ) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

}
