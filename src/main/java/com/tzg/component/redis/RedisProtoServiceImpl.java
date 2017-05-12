package com.tzg.component.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisProtoServiceImpl< K, V > implements RedisProtoService< K, V > {

    @Resource
    private RedisTemplate< Serializable, Object > redisTemplate;

    @Override
    public boolean has( K key ) {
        return redisTemplate.hasKey( ( Serializable ) key );
    }

    @Override
    public void put( K key, V value ) {
        redisTemplate.opsForValue().set( ( Serializable ) key, value );
    }

    @Override
    public void putList( K key, List< ? > list ) {
        redisTemplate.delete( ( Serializable ) key );
        ListOperations< Serializable, Object > operations = redisTemplate.opsForList();
        for ( int i = 0; i < list.size(); i++ ) {
            operations.rightPush( ( Serializable ) key, list.get( i ) );
        }
    }

    @Override
    public void putMap( String key, Map< ? extends K, ? extends V > map ) {
        redisTemplate.delete( key );
        redisTemplate.opsForHash().putAll( key, map );
    }

    @Override
    public void putSet( String key, Set< ? > set ) {
        SetOperations< Serializable, Object > operations = redisTemplate.opsForSet();
        redisTemplate.delete( key );
        Iterator< ? > it = set.iterator();
        while ( it.hasNext() ) {
            operations.add( key, it.next() );
        }
    }

    @Override
    public void putZSet( String key, Set< ? > set ) {
        redisTemplate.delete( key );
        ZSetOperations< Serializable, Object > operations = redisTemplate.opsForZSet();
        Iterator< ? >                          it         = set.iterator();
        int                                    i          = 0;
        while ( it.hasNext() ) {
            operations.add( key, it.next(), i++ );
        }
    }

    @Override
    public void delete( K key ) {
        redisTemplate.delete( ( Serializable ) key );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public V select( K key ) {
        return ( V ) redisTemplate.opsForValue().get( key );
    }

    @Override
    public List< ? > selectList( String key ) {
        Long size = redisTemplate.opsForList().size( key );
        return redisTemplate.opsForList().range( key, 0, size );
    }

    @Override
    public Map< ?, ? > get( String key ) {
        return redisTemplate.opsForHash().entries( key );
    }

    @Override
    public Set< ? > getSet( String key ) {
        return redisTemplate.opsForSet().members( key );
    }

    @Override
    public Set< ? > getZSet( String key ) {
        ZSetOperations< Serializable, Object > operations = redisTemplate.opsForZSet();
        Long                                   size       = operations.size( key );
        return operations.rangeByScore( key, 0, size );
    }


}
