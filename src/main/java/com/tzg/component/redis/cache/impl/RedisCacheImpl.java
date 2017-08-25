package com.tzg.component.redis.cache.impl;

import com.tzg.component.redis.support.service.api.RedisService;
import com.tzg.tool.support.serialize.HessianUtil;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * redis的cache实现
 */
public class RedisCacheImpl implements Cache, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger( RedisCacheImpl.class );

    //名称
    private String name;

    //默认缓存时效时间 30分钟
    private int defaultCacheExpire = 30 * 60;

    private RedisService redisService;

    /**
     * 缓存的名字
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 得到底层使用的缓存
     */
    @Override
    public Object getNativeCache() {
        return redisService;
    }

    /**
     * 根据key得到一个ValueWrapper，然后调用其get方法获取值
     */
    @Override
    public ValueWrapper get( Object key ) {
        if ( null == key ) {
            logger.warn( "key cannot be null." );
            return null;
        }
        byte[] value = redisService.get( toByte( key ) );
        if ( value == null ) {
            logger.warn( "key=[{}],value is null", key );
            return null;
        }
        Object val = HessianUtil.deserialize( value );
        logger.debug( "get value=[{}] from redis by key=[{}]", val, key );
        return new SimpleValueWrapper( val );
    }

    /**
     * 根据key、类型获取value
     */
    @Override
    public < T > T get( Object key, Class< T > type ) {
        T value = redisService.get( toString( key ), type );
        logger.debug( "get value=[{}] from redis by key=[{}]", value, toByte( key ) );
        return value;
    }

    @Override
    public < T > T get( Object key, Callable< T > callable ) {
        return null;
    }

    /**
     * 设置缓存
     */
    @Override
    public void put( Object key, Object value ) {
        redisService.set( toByte( key ), HessianUtil.serialize( value ), getDefaultCacheExpire() );
    }

    /**
     * 如果值不存在，则添加,存在则返回
     */
    @Override
    public ValueWrapper putIfAbsent( Object key, Object value ) {
        String keyf = toString( key );
        if ( redisService.exists( keyf ) ) {
            return get( key );
        }
        this.put( key, value );
        return get( key );
    }

    /**
     * 根据key删缓存数据
     */
    @Override
    public void evict( Object key ) {
        redisService.del( toString( key ) );
    }

    private byte[] toByte( Object object ) {
        if ( object instanceof String ) {
            return object.toString().getBytes( Charsets.UTF_8 );
        }
        return HessianUtil.serialize( object );
    }

    public String toString( Object object ) {
        if ( object instanceof byte[] ) {
            return HessianUtil.deserialize( ( ( byte[] ) object ) ).toString();
        }
        return object.toString();
    }

    /**
     * 清空数据
     */
    @Override
    public void clear() {
        logger.warn( "Ignore cache clear operations." );
    }

    /**
     * 参数检查
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if ( redisService == null ) {
            throw new IllegalArgumentException( "property [redisService] cannot be null." );
        }
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getDefaultCacheExpire() {
        return defaultCacheExpire;
    }

    public void setDefaultCacheExpire( int defaultCacheExpire ) {
        this.defaultCacheExpire = defaultCacheExpire;
    }

    public RedisService getRedisService() {
        return redisService;
    }

    public void setRedisService( RedisService redisService ) {
        this.redisService = redisService;
    }

}
