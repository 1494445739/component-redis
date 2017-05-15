package com.tzg.component.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisProtoService< K, V > {

    boolean has( K key );

    void put( K key, V value );

    void putList( K key, List< ? > list );

    void putMap( String key, Map< ? extends K, ? extends V > map );

    void putSet( String key, Set< ? > set );

    void putZSet( String key, Set< ? > set );

    void delete( K key );

    V select( K key );

    List< ? > selectList( String key );

    Map< ?, ? > getMap( String key );

    Set< ? > getSet( String key );

    Set< ? > getZSet( String key );

}
