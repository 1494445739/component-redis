package com.tzg.component.redis.support.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;

public class JedisUtils {

    private static final Logger logger = LoggerFactory.getLogger( JedisUtils.class );

    private static final String OK_CODE       = "OK";
    private static final String OK_MULTI_CODE = "+OK";

    /**
     * 判断 返回值是否ok.
     */
    public static boolean isStatusOk( String status ) {
        return ( status != null ) && ( OK_CODE.equals( status ) || OK_MULTI_CODE.equals( status ) );
    }

    public static byte[] toBytes( final String key ) {
        if ( null == key ) {
            return null;
        }
        return key.getBytes( Charset.forName( "UTF-8" ) );
    }

    /**
     * 在Pool以外强行销毁Jedis.
     */
    public static void destroyJedis( Jedis jedis ) {
        if ( ( jedis != null ) && jedis.isConnected() ) {
            try {
                try {
                    jedis.quit();
                } catch ( Exception e ) {
                    logger.error( "退出redis失败，失败原因是{}", e.getMessage() );
                }
                jedis.disconnect();
            } catch ( Exception e ) {
                logger.error( "断开redis失败，失败原因是{}", e.getMessage() );
            }
        }
    }

}
