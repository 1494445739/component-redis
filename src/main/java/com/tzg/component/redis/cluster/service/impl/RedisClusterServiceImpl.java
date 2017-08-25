package com.tzg.component.redis.cluster.service.impl;

import com.tzg.component.redis.support.service.api.RedisService;
import com.tzg.tool.support.serialize.HessianUtil;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import java.util.*;

import com.tzg.component.redis.support.util.JedisUtils;

/**
 * redis客户端集群操作方法封装
 */
public class RedisClusterServiceImpl implements RedisService {

    private static final Logger logger = LoggerFactory.getLogger( RedisClusterServiceImpl.class );

    private JedisCluster jedisCluster;

    public void setJedisCluster( JedisCluster jedisCluster ) {
        this.jedisCluster = jedisCluster;
    }

    /**
     * 删除给定的一个或多个key，不存在的 key 会被忽略。
     *
     * @param keys 一个或多个key ，传入null时抛出异常
     * @return
     */
    public Boolean del( final String... keys ) {
        Assert.isTrue( null != keys && keys.length >= 1, "del操作不允许key为空!指定一个或多个key!" );
        try {
            return jedisCluster.del( keys ) == keys.length;
        } catch ( Exception e ) {
            logger.error( "del keys:{}", keys, e );
        }
        return false;
    }

    /**
     * 获取long值，key不存在时返回null
     *
     * @param key
     * @return
     */
    public Long getAsLong( final String key ) {
        try {
            String result = get( key );
            return result != null ? Long.valueOf( result ) : null;
        } catch ( NumberFormatException e ) {
            logger.error( "getAsLong key:{}", key, e );
        }
        return null;
    }

    /**
     * 获取int值，key不存在时返回null
     *
     * @param key
     * @return
     */
    public Integer getAsInt( final String key ) {
        try {
            return getAsInt( key, null );
        } catch ( Exception e ) {
            logger.error( "getAsInt key:{}", key, e );
        }
        return null;
    }

    /**
     * 获取int值，key不存在或数据类型错误时返回默认值
     *
     * @param key
     * @return
     */
    public Integer getAsInt( final String key, Integer iDefault ) {
        String result = get( key );
        if ( StringUtils.isBlank( result ) ) {
            return iDefault;
        }
        try {
            return Integer.valueOf( result );
        } catch ( Exception e ) {
            return iDefault;
        }
    }

    /**
     * 返回所有(一个或多个)给定 key 的值，如给定多个keys里面，有某个key不存在，那么这个 key返回特殊值nil
     *
     * @param keys 一个或多个key,传入null时抛出异常
     * @return 一个包含所有给定 key 的值的列表。
     */
    public List< String > mget( final String... keys ) {
        Assert.isTrue( null != keys && keys.length >= 1, "del操作不允许key为空!指定一个或多个key!" );
        try {
            return jedisCluster.mget( keys );
        } catch ( Exception e ) {
            logger.error( "mget keys:{}", keys, e );
        }
        return null;
    }

    /**
     * 将key中储存的数字值增一
     * <p>
     * 如key不存在，那么key的值会先被初始化为0,然后再执行INCR 操作
     * 如值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误
     * </p>
     *
     * @param key
     * @return 执行INCR之后key的值
     */
    public Long incr( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.incr( key );
        } catch ( Exception e ) {
            logger.error( "incr key:{}", key, e );
        }
        return null;
    }

    /**
     * 将key所储存的值加上增量increment
     * <p>
     * 如key不存在,那么key的值会先被初始化为0,然后再执行 INCRBY命令。
     * 如值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * </p>
     *
     * @param key
     * @param increment 增量值
     * @return 加上increment 之后， key 的值
     */
    public Long incrBy( final String key, final long increment ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.incrBy( key, increment );
        } catch ( Exception e ) {
            logger.error( "incrBy key:{},increment:{}", key, increment, e );
        }
        return null;
    }

    /**
     * 为key中所储存的值加上浮点数增量 increment
     * <p>
     * 如key不存在，那么 INCRBYFLOAT 会先将 key 的值设为 0 ，再执行加法操作。
     * 如果命令执行成功，那么 key 的值会被更新为（执行加法之后的）新值，并且新值会以字符串的形式返回给调用者。
     * </p>
     * 以下任意一个条件发生时，返回一个错误：
     * <p>
     * key 的值不是字符串类型(因为 Redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     * key 当前的值或者给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number）
     * </p>
     *
     * @param key
     * @param increment
     * @return 执行命令之后 key的值,无论计算所得的浮点数的实际精度有多长,计算结果最多只能表示小数点的后十七位
     */
    public Double incrByFloat( final String key, final double increment ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.incrByFloat( key, increment );
        } catch ( Exception e ) {
            logger.error( "incrByFloat key:{},increment:{}", key, increment, e );
        }
        return null;
    }

    /**
     * 将key中储存的数字值减一。
     * <p>
     * 如果 key不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * </p>
     *
     * @param key
     * @return 执行DECR命令之后 key的值
     */
    public Long decr( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.decr( key );
        } catch ( Exception e ) {
            logger.error( "decr key:{}", key, e );
        }
        return null;
    }

    /**
     * 将key所储存的值减去减量 decrement 。
     * <p>
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * </p>
     *
     * @param key
     * @param decrement
     * @return 减去 decrement 之后， key 的值
     */
    public Long decrBy( final String key, final long decrement ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.decrBy( key, decrement );
        } catch ( Exception e ) {
            logger.error( "decrBy key:{},decrement:{}", key, e );
        }
        return null;
    }

    /**
     * 获取字符串值
     *
     * @param key
     * @return key对应的值不存在时返回null；如key对应存储的值类型不是字符串，返回异常
     */
    public String get( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.get( key );
        } catch ( Exception e ) {
            logger.error( "get key:{}", key, e );
        }
        return null;
    }

    @Override
    public byte[] get( byte[] key ) {
        try {
            return jedisCluster.get( key );
        } catch ( Exception e ) {
            logger.error( "get key:{}", key, e );
        }
        return null;
    }

    /**
     * 将字符串值 value 关联到 key 。
     * <p>
     * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     * </p>
     *
     * @param key
     * @param value
     */
    public void set( final String key, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            jedisCluster.set( key, value );
        } catch ( Exception e ) {
            logger.error( "set key:{},value:{}", key, value, e );
        }
    }

    /**
     * 将字符串值 value 关联到 key，并设置过期时间
     *
     * @param key
     * @param value
     * @param seconds 过期时间，单位秒
     */
    @Override
    public void set( final String key, final String value, final int seconds ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            boolean ok = JedisUtils.isStatusOk( jedisCluster.set( key, value ) );
            if ( ok && seconds > 0 ) {
                jedisCluster.expire( key, seconds );
            }
        } catch ( Exception e ) {
            logger.error( "set key:{},value:{},seconds:{}", key, value, seconds, e );
        }
    }

    /**
     * 设置值和过期时间，如果key对应的值不存在则设置,已经存在则更新
     *
     * @param key
     * @param value
     * @param seconds 过期时间，单位秒
     */
    public void setex( final String key, final String value, final int seconds ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            jedisCluster.setex( key, seconds, value );
        } catch ( Exception e ) {
            logger.error( "setex key:{},value:{},seconds:{}", key, value, seconds, e );
        }
    }

    public void set( final byte[] key, final byte[] value, final int seconds ) {
        try {
            boolean ok = JedisUtils.isStatusOk( jedisCluster.set( key, value ) );
            if ( ok && seconds > 0 ) {
                jedisCluster.expire( key, seconds );
            }
        } catch ( Exception e ) {
            logger.error( "set key:{},value:{},seconds:{}", key, value, seconds, e );
        }
    }

    /**
     * 当key不存在时,设置值.key已经存在时不做处理
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean setnx( final String key, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            return jedisCluster.setnx( key, value ) == 1;
        } catch ( Exception e ) {
            logger.error( "setnx key:{},value:{}", key, value, e );
        }
        return false;
    }

    /**
     * 当key不存在时,设置值并设置过期时间.key已经存在时不做处理
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean setnx( final String key, final String value, final int seconds ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            String result = jedisCluster.set( key, value, "NX", "EX", seconds );
            return JedisUtils.isStatusOk( result );
        } catch ( Exception e ) {
            logger.error( "setnx key:{},value:{},seconds:{}", key, value, seconds, e );
        }
        return false;
    }

    /**
     * 将给定key的值设为value ，并返回 key的旧值(old value)。
     *
     * @param key
     * @param value
     * @return
     */
    public String getSet( final String key, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.getSet( key, value );
        } catch ( Exception e ) {
            logger.error( "getSet key:{},value:{}", key, value, e );
        }
        return null;
    }

    //Hash(哈希表):HDEL、HEXISTS、 HGET、HGETALL、HINCRBY、 HINCRBYFLOAT、HKEYS、HLEN、HMGET、HMSET、HSET、HSETNX、HVALS、HSCAN
    /**
     * 同时将多个field-value(域-值)对设置到哈希表 key 中。此命令会覆盖哈希表中已存在的域。如果 key不存在，一个空哈希表被创建并执行HMSET操作
     *
     * @param key
     * @param map
     */
    @Override
    public Boolean hmset( final String key, final Map< String, String > map ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            //如果命令执行成功，返回 OK 。 当 key 不是哈希表(hash)类型时，返回一个错误。
            return JedisUtils.isStatusOk( jedisCluster.hmset( key, map ) );
        } catch ( Exception e ) {
            logger.error( "hmset key:{},map:{}", key, map, e );
        }
        return false;
    }

    @Override
    public Boolean hmset( String key, Map< String, String > map, int seconds ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            //如果命令执行成功，返回 OK 。 当 key 不是哈希表(hash)类型时，返回一个错误。
            boolean flag = JedisUtils.isStatusOk( jedisCluster.hmset( key, map ) );
            if ( flag && seconds > 0 ) {
                flag = jedisCluster.expire( key, seconds ) == 1;
            }
            return flag;
        } catch ( Exception e ) {
            logger.error( "hmset key:{},map:{}", key, map, e );
        }
        return false;
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     *
     * @param key
     * @param fields 一个或多个域
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    public List< String > hmget( final String key, final String... fields ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        Assert.isTrue( null != fields && fields.length >= 1, "hmget操作不允许fields为空!指定一个或多个field!" );
        try {
            return jedisCluster.hmget( key, fields );
        } catch ( Exception e ) {
            logger.error( "hmget key:{},fields:{}", key, fields, e );
        }
        return null;
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key
     * @param field
     * @return 给定域的值。当给定域不存在或是给定 key不存在时，返回 nil
     */
    public String hget( final String key, final String field ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        try {
            return jedisCluster.hget( key, field );
        } catch ( Exception e ) {
            logger.error( "hget key:{},field:{}", key, field, e );
        }
        return null;
    }

    @Override
    public < T > Object hget( String key, String fieldName, Class< T > clazz ) {
        try {
            if ( StringUtils.isBlank( key ) || StringUtils.isBlank( fieldName ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            byte[] bytes = jedisCluster.hget( JedisUtils.toBytes( key ), JedisUtils.toBytes( fieldName ) );
            if ( bytes == null || bytes.length == 0 ) {
                return null;
            }
            return HessianUtil.deserialize( bytes );
        } catch ( Exception e ) {
            logger.error( "hget key:{},field:{}", key, fieldName, e );
        }
        return null;
    }

    /**
     * 返回哈希表key中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     *
     * @param key
     * @return 返回哈希表的域和域的值, 若 key不存在，返回空列表。
     */
    public Map< String, String > hgetAll( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.hgetAll( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public < V > Map< String, V > hgetAll( String key, Class< V > clazz ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Map< String, V >      result  = new HashMap<>();
            Map< byte[], byte[] > byteMap = jedisCluster.hgetAll( JedisUtils.toBytes( key ) );
            for ( byte[] bytes : byteMap.keySet() ) {
                result.put( new String( bytes, Charsets.UTF_8 ), ( V ) HessianUtil.deserialize( byteMap.get( bytes ) ) );
            }
            return result;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public List< String > hvals( final String key ) {
        return jedisCluster.hvals( key );
    }

    /**
     * 将哈希表 key中的域field的值设为 value 。
     * 如果 key不存在，一个新的哈希表被创建并进行HSET操作。
     * 如果域 field已经存在于哈希表中,旧值将被覆盖。
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset( final String key, final String field, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            //如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 . 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
            jedisCluster.hset( key, field, value );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }

    }

    @Override
    public void hset( String key, String fieldName, Object value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            jedisCluster.hset( JedisUtils.toBytes( key ), JedisUtils.toBytes( fieldName ), HessianUtil.serialize( value ) );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
     * 若域 field 已经存在，该操作无效。
     * 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
     *
     * @param key
     * @param field
     * @param value
     * @return 设置成功，返回 1 。如果给定域已经存在且没有操作被执行，返回 0 。
     */
    public Boolean hsetnx( final String key, final String field, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            return jedisCluster.hsetnx( key, field, value ) == 1;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return false;
    }

    /**
     * 为哈希表 key中的域 field的值加上增量 increment 。
     * <p>
     * 如果 key不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
     * 如果域 field不存在，那么在执行命令前，域的值被初始化为 0 。
     * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
     * </p>
     *
     * @param key
     * @param field
     * @param increment 增量,也可以为负数，相当于对给定域进行减法操作。
     * @return
     */
    public Long hincrBy( final String key, final String field, final long increment ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.hincrBy( key, field, increment );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 为哈希表 key 中的域 field 加上浮点数增量 increment 。
     * 如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。
     * 如果键 key 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。
     * 当以下任意一个条件发生时，返回一个错误：
     * 域 field 的值不是字符串类型(因为 redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     * 域 field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number)
     *
     * @param key
     * @param field
     * @param increment
     * @return 执行加法操作之后 field 域的值
     */
    public Double hincrByFloat( final String key, final String field, final double increment ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.hincrByFloat( key, field, increment );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key
     * @param fields
     * @return 被成功移除的域的数量，不包括被忽略的域
     */
    public Long hdel( final String key, final String... fields ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Assert.isTrue( null != fields && fields.length >= 1, "hdel操作不允许fields为空!指定一个或多个field!" );
            return jedisCluster.hdel( key, fields );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在。
     *
     * @param key
     * @param field
     * @return 如果哈希表含有给定域，返回 1 。如果哈希表不含有给定域，或 key 不存在，返回 0 。
     */
    public Boolean hexists( final String key, final String field ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.hexists( key, field );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回哈希表 key中的所有域。
     *
     * @param key
     * @return 一个包含哈希表中所有域的表。当 key 不存在时，返回一个空表。
     */
    public Set< String > hkeys( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.hkeys( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回哈希表 key 中域的数量。
     *
     * @param key
     * @return 哈希表中域的数量。当 key 不存在时，返回 0 。
     */
    public Long hlen( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.hlen( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    // List(列表):BLPOP、BRPOP、BRPOPLPUSH、LINDEX、LINSERT、LLEN、LPOP、LPUSH、LPUSHX、LRANGE、LREM、LSET、LTRIM、RPOP、RPOPLPUSH、RPUSH、RPUSHX
    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个value值，那么各个 value 值按从左到右的顺序依次插入到表头.
     * 比如说，对空列表 mylist 执行命令:
     * LPUSH mylist a b c
     * LRANGE mylist 0 -1
     * 列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key
     * @param values 1个(至少)或多个值
     * @return
     */
    public Long lpush( final String key, final String... values ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Assert.isTrue( null != values && values.length >= 1, "lpush操作不允许values为空!指定一个或多个value!" );
            return jedisCluster.lpush( key, values );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public Long lpush( final byte[] key, final byte[]... values ) {
        try {
            if ( null == key || key.length == 0 ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Assert.isTrue( null != values && values.length >= 1, "lpush操作不允许values为空!指定一个或多个value!" );
            return jedisCluster.lpush( key, values );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * BRPOP 是列表的阻塞式(blocking)弹出原语。
     * 它是 RPOP命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
     * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的尾部元素。
     *
     * @param timeout
     * @param key
     * @return
     */
    public String brpop( final int timeout, final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            List< String > list = jedisCluster.brpop( timeout, key );
            return null == list ? null : list.get( 1 );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public Long rpush( final byte[] key, final byte[]... values ) {
        try {
            if ( null == key || key.length == 0 ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Assert.isTrue( null != values && values.length >= 1, "lpush操作不允许values为空!指定一个或多个value!" );
            return jedisCluster.rpush( key, values );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * 举个例子，你有两个列表 source 和 destination ， source 列表有元素 a, b, c ， destination 列表有元素 x, y, z ，执行 RPOPLPUSH source destination 之后， source 列表包含元素 a, b ， destination 列表包含元素 c, x, y, z ，并且元素 c 会被返回给客户端。
     * 如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     * 如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     * Not support for sharding.
     *
     * @param srcKey
     * @param dstKey
     * @return 被弹出的元素
     */
    public String rpoplpush( final String srcKey, final String dstKey ) {
        try {
            if ( null == srcKey || null == dstKey ) {
                logger.warn( "key({}/{}) cann't be null!", srcKey, dstKey );
                return null;
            }
            return jedisCluster.rpoplpush( srcKey, dstKey );
        } catch ( Exception e ) {
            logger.error( "srcKey:{},dstKey:{}", srcKey, dstKey, e );
        }
        return null;
    }

    /**
     * BRPOPLPUSH 是 RPOPLPUSH 的阻塞版本，当给定列表 source 不为空时， BRPOPLPUSH 的表现和 RPOPLPUSH 一样。
     * 当列表 source 为空时， BRPOPLPUSH 命令将阻塞连接，直到等待超时，或有另一个客户端对 source 执行 LPUSH 或 RPUSH 命令为止。
     * 超时参数 timeout 接受一个以秒为单位的数字作为值。超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 。
     * Not support for sharding.
     *
     * @param source
     * @param destination
     * @param timeout
     * @return 假如在指定时间内没有任何元素被弹出，则返回一个 nil 和等待时长。反之，返回一个含有两个元素的列表，第一个元素是被弹出元素的值，第二个元素是等待时长。
     */
    public String brpoplpush( final String source, final String destination, final int timeout ) {
        try {
            return jedisCluster.brpoplpush( source, destination, timeout );
        } catch ( Exception e ) {
            logger.error( "source:{},destination:{}", source, e );
        }
        return null;
    }

    /**
     * 返回列表 key 的长度。
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @param key
     * @return
     */
    public Long llen( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.llen( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key   如果 key 不是列表类型，返回一个错误。
     * @param index 下标(index) 都以0为底,也可以使用负数下标，以 -1 表示列表的最后一个元素
     * @return 列表中下标为 index 的元素。如果 index 参数的值不在列表的区间范围内(out of range)，返回 nil 。
     */
    public String lindex( final String key, final long index ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.lindex( key, index );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public List< String > lrangeAll( final String key ) {
        return lrange( key, 0, -1 );
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以0为底,也可以使用负数下标，以 -1 表示列表的最后一个元素
     *
     * @param key
     * @param start
     * @param end
     * @return 一个列表，包含指定区间内的元素。
     */
    public List< String > lrange( final String key, final int start, final int end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.lrange( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public List< byte[] > lrange( final byte[] key, final int start, final int end ) {
        try {
            if ( null == key || 0 == key.length ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.lrange( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 例如执行命令：LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 当 key 不是列表类型时，返回一个错误。
     *
     * @param key
     * @param start
     * @param end
     */
    @Override
    public void ltrim( final String key, final int start, final int end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            //命令执行成功时，返回 ok 。
            jedisCluster.ltrim( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
    }

    /**
     * 保留列表下标0到size-1的数据，其他删除
     *
     * @param key  唯一标识
     * @param size 保留列表的大小
     */
    @Override
    public void ltrimFromLeft( String key, int size ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return;
            }
            jedisCluster.ltrim( key, 0, size - 1 );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
    }

    /**
     * 移除列表中与参数value相等的第一个元素
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean lremFirst( final String key, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            return jedisCluster.lrem( key, 1, value ) == 1;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 移除列表中与参数value相等的所有元素
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean lremAll( final String key, final String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            return jedisCluster.lrem( key, 0, value ) > 0;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * 当 key 不是集合类型时，返回一个错误。
     *
     * @param key
     * @param member
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素
     */
    public Boolean sadd( final String key, final String... members ) {
        Assert.isTrue( null != members && members.length >= 1, "sadd操作不允许members为空!指定一个或多个member!" );
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            return jedisCluster.sadd( key, members ) == members.length;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回集合 key 中的所有成员。不存在的 key 被视为空集合。
     *
     * @param key
     * @return 集合中的所有成员。
     */
    public Set< String > smembers( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.smembers( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 将一个或多个member元素及其score值加入到有序集key当中。
     * 如果某个member已经是有序集的成员，那么更新这个member的score值，并通过重新插入这个member元素，来保证该member在正确的位置上。
     *
     * @param key    如果key不存在，则创建一个空的有序集并执行 ZADD 操作;当key存在但不是有序集类型时，返回一个错误。
     * @param score  值可以是整数值或双精度浮点数。
     * @param member 被加入的元素
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
     */
    public Boolean zadd( final String key, final double score, final String member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            //被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
            return jedisCluster.zadd( key, score, member ) == 1;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public Boolean zadd( final String key, final double score, final Object member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            //被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
            return jedisCluster.zadd( JedisUtils.toBytes( key ), score, HessianUtil.serialize( member ) ) == 1;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集key中，成员member的score值
     *
     * @param key    key不存在，返回nil
     * @param member 被加入的元素,如果 member元素不是有序集 key的成员，返回nil
     * @return member 成员的 score 值
     */
    public Double zscore( final String key, final String member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zscore( key, member );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 获得成员按score值递减(从大到小)排列的排名,排名以 0 为底
     *
     * @param key
     * @param member
     * @return 如果 member是有序集 key 的成员，返回 member 的排名。如果 member 不是有序集 key 的成员，返回 nil 。
     */
    public Long zrank( final String key, final String member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrank( key, member );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 获得成员按score值递减(从大到小)排列的排名,排名以0为底,score值最大的成员排名为 0
     *
     * @param key
     * @param member
     * @return 如果 member是有序集 key 的成员，返回 member 的排名。如果 member 不是有序集 key 的成员，返回 nil 。
     */
    public Long zrevrank( final String key, final String member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrevrank( key, member );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     *
     * @param key
     * @param min
     * @param max
     * @return score 值在 min 和 max 之间的成员的数量。
     */
    public Long zcount( final String key, final double min, final double max ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zcount( key, min, max );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key中，指定区间内的成员。其中成员的位置按 score值递增(从小到大)来排序。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，有序集成员的列表。
     */
    public Set< String > zrange( final String key, final int start, final int end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrange( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public < T > Set< T > zrange( final String key, final int start, final int end, Class< T > claz ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Set< T >      set   = new LinkedHashSet<>();
            Set< byte[] > bytes = jedisCluster.zrange( JedisUtils.toBytes( key ), start, end );
            if ( null == bytes ) {
                return set;
            }
            for ( byte[] b : bytes ) {
                Object val = HessianUtil.deserialize( b );
                if ( null == val ) {
                    logger.warn( "byte:{} deserialize is null", Arrays.toString( b ) );
                    continue;
                }
                set.add( ( T ) val );
            }
            return set;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，带有 score值的有序集成员的列表。
     */
    public Set< Tuple > zrangeWithScores( final String key, final int start, final int end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrangeWithScores( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。其中成员的位置按 score 值递减(从大到小)来排列。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     */
    public Set< String > zrevrange( final String key, final int start, final int end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrevrange( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     */
    public Set< Tuple > zrevrangeWithScores( final String key, final int start, final int end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrevrangeWithScores( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set< String > zrangeByScore( final String key, final double min, final double max ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrangeByScore( key, min, max );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public Set< Tuple > zrangeByScoreWithScores( final String key, final double min, final double max ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrangeByScoreWithScores( key, min, max );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     *
     * @param key
     * @param max
     * @param min
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     */
    public Set< String > zrevrangeByScore( final String key, final double max, final double min ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrevrangeByScore( key, max, min );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public Set< Tuple > zrevrangeByScoreWithScores( final String key, final double max, final double min ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zrevrangeByScoreWithScores( key, max, min );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key
     * @param member
     * @return 被成功移除的成员的数量，不包括被忽略的成员。
     */
    public Boolean zrem( final String key, final String member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return false;
            }
            return jedisCluster.zrem( key, member ) == 1;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     *
     * @param key
     * @param start
     * @param end
     * @return 被移除成员的数量。
     */
    public Long zremByScore( final String key, final double start, final double end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zremrangeByScore( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员。区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
     *
     * @param key
     * @param start 以0开始,以-1表示最后一个成员， -2表示倒数第二个成员
     * @param end   以0开始,以-1表示最后一个成员， -2表示倒数第二个成员
     * @return 被移除成员的数量
     */
    public Long zremByRank( final String key, final long start, final long end ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zremrangeByRank( key, start, end );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    /**
     * 返回有序集 key 的基数
     *
     * @param key
     * @return 当 key 存在且是有序集类型时，返回有序集的基数。当 key 不存在时，返回 0 。
     */
    public Long zcard( final String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.zcard( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public boolean exists( String... keys ) {
        Assert.isTrue( null != keys && keys.length >= 1, "exists操作不允许key为空!指定一个或多个key!" );
        try {
            return jedisCluster.exists( keys ) == keys.length;
        } catch ( Exception e ) {
            logger.error( "keys:{}", keys, e );
        }
        return false;
    }

    @Override
    public < T > List< T > getAsList( String key, Class< T > clazz ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            byte[] bytes = jedisCluster.get( JedisUtils.toBytes( key ) );
            return null == bytes ? null : ( List< T > ) HessianUtil.deserialize( bytes );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public < T > T get( String key, Class< T > clazz ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            byte[] bytes = jedisCluster.get( JedisUtils.toBytes( key ) );
            return ( T ) ( null == bytes ? null : HessianUtil.deserialize( bytes ) );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public Boolean set( String key, List< ? > list, int seconds ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            String  status = jedisCluster.set( JedisUtils.toBytes( key ), HessianUtil.serialize( list ) );
            boolean ok     = JedisUtils.isStatusOk( status );
            if ( ok && seconds > 0 ) {
                ok = jedisCluster.expire( JedisUtils.toBytes( key ), seconds ) == 1;
            }
            return ok;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public Boolean set( String key, Object object, int seconds ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            String  status = jedisCluster.set( JedisUtils.toBytes( key ), HessianUtil.serialize( object ) );
            boolean ok     = JedisUtils.isStatusOk( status );
            if ( ok && seconds > 0 ) {
                ok = jedisCluster.expire( JedisUtils.toBytes( key ), seconds ) == 1;
            }
            return ok;
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public Boolean lset( String key, long index, String value ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return JedisUtils.isStatusOk( jedisCluster.lset( key, index, value ) );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    public String lpop( final String key ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        return jedisCluster.lpop( key );
    }

    public byte[] lpop( final byte[] key ) {
        if ( null == key || 0 == key.length ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        return jedisCluster.lpop( key );
    }

    @Override
    public String rpop( String key ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.rpop( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public byte[] rpop( byte[] key ) {
        try {
            if ( null == key || 0 == key.length ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.rpop( key );
        } catch ( Exception e ) {
            logger.error( "key:{}", key, e );
        }
        return null;
    }

    @Override
    public Boolean sadd( String key, String member ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            return jedisCluster.sadd( key, member ) == 1;
        } catch ( Exception e ) {
            logger.error( "key:{},member:{}", key, member, e );
        }
        return null;
    }

    @Override
    public Long publish( String channel, String message ) {
        try {
            if ( StringUtils.isBlank( channel ) ) {
                logger.warn( "channel({}) cann't be blank(null or empty)!", channel );
                return null;
            }
            return jedisCluster.publish( channel, message );
        } catch ( Exception e ) {
            logger.error( "channel:{},message:{}", channel, message, e );
        }
        return null;
    }

    @Override
    public String incrByleng( String key, int length ) {
        try {
            if ( StringUtils.isBlank( key ) ) {
                logger.warn( "key({}) cann't be blank(null or empty)!", key );
                return null;
            }
            Long l      = jedisCluster.incr( key );
            Long maxnum = ( long ) ( Math.pow( 10, length ) - 1 );
            if ( l >= maxnum ) {
                jedisCluster.del( key );
            }
            String str = String.format( "%0" + length + "d", l );
            return str;
        } catch ( Exception e ) {
            logger.error( "incr key:{}", key, e );
        }
        return null;
    }

    @Override
    public Long expire( final String key, final int seconds ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        return jedisCluster.expire( key, seconds );
    }

    @Override
    public Long expireAt( final String key, final long unixTime ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        return jedisCluster.expireAt( key, unixTime );

    }

    @Override
    public Long pexpireat( final String key, final long millisecondsTimestamp ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        return jedisCluster.pexpireAt( key, millisecondsTimestamp );
    }

    @Override
    public Long persist( final String key ) {
        if ( StringUtils.isBlank( key ) ) {
            logger.warn( "key({}) cann't be blank(null or empty)!", key );
            return null;
        }
        return jedisCluster.persist( key );
    }

}
