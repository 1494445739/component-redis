package com.tzg.component.redis.support.service.api;

import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis常用操作方法封装
 */
public interface RedisService {

    /**
     * 判断key是否在redis中存在
     *
     * @param key 一个或多个key，不传时抛出异常
     */
    boolean exists( String... key );

    /**
     * 获取key所关联的字符串值
     *
     * @param key 键，唯一标识
     * @return 返回key所关联的字符串值，如果key不存在那么返回特殊值nil；如key储存的值不是字符串类型，返回一个错误
     */
    String get( String key );

    /**
     * 获取key所关联的值
     *
     * @param key 键，唯一标识
     * @return 返回key所关联的值，如果key不存在那么返回特殊值nil；如key储存的值类型不匹配，返回一个错误。
     */
    byte[] get( byte[] key );

    /**
     * 根据key、class获取对应的数据
     *
     * @param key   键，唯一标识
     * @param clazz 类
     * @return
     */
    < T > T get( String key, Class< T > clazz );

    /**
     * 获取key关联的值
     *
     * @param key 键，唯一标识
     * @return 返回 key所关联的值，如果key不存在那么返回特殊值nil；如key储存的值不是int类型，返回一个错误。
     */
    Integer getAsInt( String key );

    /**
     * 获取key关联的值
     *
     * @param key
     * @param iDefault key不存在时或数据类型错误时，返回默认值
     * @return
     */
    Integer getAsInt( String key, Integer iDefault );

    /**
     * 获取key关联的值
     *
     * @param key
     * @return 返回key所关联的值。如果key不存在那么返回特殊值nil；如key储存的值不是long类型，返回一个错误。
     */
    Long getAsLong( String key );

    /**
     * 根据key获取列表数据
     *
     * @param key 键、唯一标识
     * @return
     */
    < T > List< T > getAsList( String key, Class< T > clazz );

    /**
     * 删除给定的一个或多个key,不存在的key会被忽略
     *
     * @param keys 一个或多个key,不传参时抛出IllegalArgumentException异常
     * @return
     */
    Boolean del( String... keys );

    /**
     * 将 key 中储存的数字值增一。如果key不存在，那么key的值会先被初始化为0 ，然后再执行INCR操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *
     * @param key
     * @return 执行 INCR命令之后key的值。
     */
    Long incr( String key );

    /**
     * 将key中储存的数字值增一。如果key不存在，那么key的值会先被初始化为0 ，然后再执行INCR操作。
     * 自增值达到长度为10的length+1次方(length=2 redis返回值=1000) 数值清空从零开始 重新自增
     * 方法返回值String。左边补0。例如: 返回值为1, length =3 方法返回值001
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *
     * @param key
     * @param length
     * @return 执行 INCR命令之后key的值。seqNO batchNO
     */
    String incrByleng( String key, int length );

    /**
     * 将key所储存的值加上增量increment, 如果key不存在，那么key的值会先被初始化为0，然后再执行INCRBY命令。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *
     * @param key
     * @param increment
     * @return 加上 increment 之后， key 的值。
     */
    Long incrBy( String key, long increment );

    /**
     * 为key中所储存的值加上浮点数增量increment。如果key不存在，那么INCRBYFLOAT会先将key的值设为0，再执行加法操作。
     * 如果命令执行成功，那么key的值会被更新为（执行加法之后的）新值。
     * 以下任意一个条件发生时，返回一个错误：
     * key值不是字符串类型(因为 Redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     * key当前的值或者给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number）
     *
     * @param key
     * @param increment
     * @return 执行命令之后 key 的值。
     */
    Double incrByFloat( String key, double increment );

    /**
     * 将key储存的数字值减一。
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行DECR操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *
     * @param key
     * @return 执行 DECR 命令之后 key 的值
     */
    Long decr( String key );

    /**
     * 将 key 所储存的值减去减量 decrement 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *
     * @param key
     * @param decrement
     * @return 减去 decrement 之后， key 的值
     */
    Long decrBy( String key, long decrement );

    /**
     * 将字符串值 value 关联到 key 。
     * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     *
     * @param key
     * @param value
     */
    void set( String key, String value );

    /**
     * 将字符串值value关联到key，并设置过期时间
     *
     * @param key     键,唯一标识
     * @param value   值
     * @param seconds 过期时间，单位秒
     */
    void set( String key, String value, int seconds );

    /**
     * 将list列表关联到key,并设置过期时间
     *
     * @param key     键,唯一标识
     * @param list    列表
     * @param seconds 过期时间
     * @return 是否设置成功
     */
    Boolean set( String key, List< ? > list, int seconds );

    /**
     * 将key关联到value，并设置过期时间
     *
     * @param key
     * @param value
     * @param seconds
     */
    void set( byte[] key, byte[] value, int seconds );

    /**
     * 将object关联到key，并设置过期时间.
     *
     * @param key
     * @param object  存储的值，注意此数据类型建议为除map、list外的数据类型
     * @param seconds
     * @return 是否设置成功
     * 获取@see {@link #get(String, Class)}
     */
    Boolean set( String key, Object object, int seconds );

    /**
     * 将值 value 关联到 key，并将key的生存时间设为seconds(以秒为单位)。如果key已经存在， SETEX命令将覆写旧值。
     *
     * @param key
     * @param value
     * @param seconds
     */
    void setex( String key, String value, int seconds );

    /**
     * 当key不存在时,设置值.key已经存在时不做处理
     *
     * @param key
     * @param value
     * @return
     */
    Boolean setnx( String key, String value );

    /**
     * 当key不存在时,设置值并设置过期时间.key已经存在时不做处理
     *
     * @param key
     * @param value
     * @return
     */
    Boolean setnx( String key, String value, int seconds );

    /**
     * 将给定key的值设为value ，并返回 key 的旧值(old value)。
     *
     * @param key
     * @param value
     * @return
     */
    String getSet( String key, String value );

    /**
     * 同时将多个field-value(域-值)对设置到哈希表 key 中。此命令会覆盖哈希表中已存在的域。如果 key不存在，一个空哈希表被创建并执行HMSET操作
     *
     * @param key 键,唯一标识.
     * @param map
     * @return 是否设置成功或当key不是哈希表(hash)类型时，返回一个错误。
     */
    Boolean hmset( String key, Map< String, String > map );

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key中并设置过期时间。
     * 此命令会覆盖哈希表中已存在的域。如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     *
     * @param key
     * @param map
     * @param seconds 超时时间,单位秒
     * @return
     */
    Boolean hmset( String key, Map< String, String > map, int seconds );

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     *
     * @param key
     * @param fields 一个或多个域
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    List< String > hmget( String key, String... fields );

    /**
     * 查看哈希表 key 中，给定域 field 是否存在。
     *
     * @param key   键
     * @param field 域
     * @return
     */
    Boolean hexists( String key, String field );

    /**
     * 返回哈希表key中给定域field的值。
     *
     * @param key
     * @param fieldName 域
     * @return 给定域的值。当给定域不存在或是给定 key不存在时，返回 nil
     */
    String hget( String key, String fieldName );

    /**
     * 返回哈希表key中给定域field的值。
     *
     * @param key
     * @param fieldName 域
     * @param clazz     返回结果的数据类型
     * @return 给定域的值。当给定域不存在或是给定 key不存在时，返回 nil
     */
    < T > Object hget( String key, String fieldName, Class< T > clazz );

    /**
     * 返回哈希表key中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     *
     * @param key
     * @return 以列表形式返回哈希表的域和域的值, 若key不存在，返回空列表。
     */
    Map< String, String > hgetAll( String key );

    < V > Map< String, V > hgetAll( String key, Class< V > clazz );

    /**
     * 获取所有域的值
     *
     * @param key
     */
    List< String > hvals( String key );

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key不存在，一个新的哈希表被创建并进行HSET操作。
     * 如果域 field已经存在于哈希表中,旧值将被覆盖。
     *
     * @param key
     * @param fieldName
     * @param value
     */
    void hset( String key, String fieldName, String value );

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中,旧值将被覆盖。
     *
     * @param key
     * @param fieldName
     * @param value
     */
    void hset( String key, String fieldName, Object value );

    /**
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
     * 若域 field 已经存在，该操作无效。
     * 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
     *
     * @param key
     * @param fieldName
     * @param value
     * @return 设置成功，返回 1 。如果给定域已经存在且没有操作被执行，返回 0 。
     */
    Boolean hsetnx( String key, String fieldName, String value );

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment 。
     * 增量也可以为负数，相当于对给定域进行减法操作。
     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
     * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
     * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
     * 本操作的值被限制在 64 位(bit)有符号数字表示之内。
     *
     * @param key
     * @param fieldName
     * @param increment
     * @return
     */
    Long hincrBy( String key, String fieldName, long increment );

    /**
     * 为哈希表 key 中的域 field 加上浮点数增量 increment 。
     * 如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。
     * 如果键 key 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。
     * 当以下任意一个条件发生时，返回一个错误：
     * 域 field 的值不是字符串类型(因为 redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     * 域 field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number)
     *
     * @param key
     * @param fieldName
     * @param increment
     * @return 执行加法操作之后 field 域的值
     */
    Double hincrByFloat( String key, String fieldName, double increment );

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key
     * @param fields
     * @return 被成功移除的域的数量，不包括被忽略的域
     */
    Long hdel( String key, String... fields );

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key
     * @return 一个包含哈希表中所有域的表。当 key 不存在时，返回一个空表。
     */
    Set< String > hkeys( String key );

    /**
     * 返回哈希表 key中域的数量。
     *
     * @param key
     * @return 哈希表中域的数量, 当 key 不存在时，返回 0
     */
    Long hlen( String key );

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
    Long lpush( String key, String... values );

    Long lpush( byte[] key, byte[]... values );

    /**
     * 将列表 key下标为index的元素的值设置为 value 。
     * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    Boolean lset( String key, long index, String value );

    String lpop( String key );

    byte[] lpop( byte[] key );

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾
     *
     * @param key
     * @param values 至少一个值
     */
    Long rpush( byte[] key, byte[]... values );

    /**
     * 返回列表 key的尾元素并移除
     *
     * @param key
     * @return
     */
    String rpop( String key );

    byte[] rpop( byte[] key );

    /**
     * BRPOP 是列表的阻塞式(blocking)弹出原语。
     * 它是 RPOP命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
     * 当给定多个 key 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的尾部元素。
     *
     * @param timeout
     * @param key
     * @return
     */
    String brpop( int timeout, String key );

    /**
     * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * 举个例子，你有两个列表 source 和 destination ， source 列表有元素 a, b, c ， destination 列表有元素 x, y, z ，
     * 执行 RPOPLPUSH source destination 之后， source 列表包含元素 a, b ， destination 列表包含元素 c, x, y, z ，
     * 并且元素 c 会被返回给客户端。
     * 如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     * 如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     * Not support for sharding.
     *
     * @param sourceKey
     * @param destinationKey
     * @return 被弹出的元素
     */
    String rpoplpush( String sourceKey, String destinationKey );

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
    String brpoplpush( String source, String destination, int timeout );

    /**
     * 返回列表 key 的长度。
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @param key
     * @return
     */
    Long llen( String key );

    /**
     * 返回列表 key 中，下标为 index 的元素。
     *
     * @param key   如果 key 不是列表类型，返回一个错误。
     * @param index 下标(index) 都以0为底,也可以使用负数下标，以 -1 表示列表的最后一个元素
     * @return 列表中下标为 index 的元素。如果 index 参数的值不在列表的区间范围内(out of range)，返回 nil 。
     */
    String lindex( String key, long index );

    /**
     * 获取list的所有数据
     *
     * @param key
     * @return
     */
    List< String > lrangeAll( String key );

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以0为底,也可以使用负数下标，以 -1 表示列表的最后一个元素
     *
     * @param key
     * @param start
     * @param end
     * @return 一个列表，包含指定区间内的元素。
     */
    List< String > lrange( String key, int start, int end );

    List< byte[] > lrange( byte[] key, int start, int end );

    /**
     * 对一个列表进行修剪(trim),让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * 下标(index)参数 start 和 stop 都以 0 为底,也可以使用负数下标，以 -1 表示列表的最后一个元素
     *
     * @param key   键，唯一标识，当 key不是列表类型时，返回一个错误。
     * @param start
     * @param end
     */
    void ltrim( String key, int start, int end );

    /**
     * 保留列表下标0到size-1的数据，其他删除
     *
     * @param key  唯一标识
     * @param size 保留列表的大小
     */
    void ltrimFromLeft( String key, int size );

    /**
     * 移除列表中与参数value相等的第一个元素
     *
     * @param key
     * @param value
     * @return
     */
    Boolean lremFirst( String key, String value );

    /**
     * 移除列表中与参数value相等的所有元素
     *
     * @param key
     * @param value
     * @return
     */
    Boolean lremAll( String key, String value );

    /**
     * 将一个或多个member元素加入到集合 key当中，已经存在于集合的member元素将被忽略。
     *
     * @param key    假如 key不存在，则创建一个只包含member元素作成员的集合;当 key不是集合类型时，返回一个错误。
     * @param member 被加入的元素
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素
     */
    Boolean sadd( String key, String member );

    /**
     * 返回集合key中的所有成员。不存在的key被视为空集合。
     *
     * @param key
     * @return 集合中的所有成员。
     */
    Set< String > smembers( String key );

    /**
     * 将一个或多个member元素及其score值加入到有序集key当中。
     * 如果某个member已经是有序集的成员，那么更新这个member的score值，并通过重新插入这个member元素，来保证该member在正确的位置上。
     *
     * @param key    如果key不存在，则创建一个空的有序集并执行 ZADD 操作;当key存在但不是有序集类型时，返回一个错误。
     * @param score  值可以是整数值或双精度浮点数。
     * @param member 被加入的元素
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
     */
    Boolean zadd( String key, double score, String member );

    /**
     * 将一个或多个member元素及其score值加入到有序集key当中。
     * 如果某个member已经是有序集的成员，那么更新这个member的score值，并通过重新插入这个member元素，来保证该member在正确的位置上。
     *
     * @param key    如果key不存在，则创建一个空的有序集并执行 ZADD 操作;当key存在但不是有序集类型时，返回一个错误。
     * @param score  值可以是整数值或双精度浮点数。
     * @param member 被加入的元素
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
     */
    Boolean zadd( String key, double score, Object member );

    /**
     * 返回有序集key中，成员member的score值
     *
     * @param key    key不存在，返回nil
     * @param member 被加入的元素,如果 member元素不是有序集 key的成员，返回nil
     * @return member 成员的 score 值
     */
    Double zscore( String key, String member );

    /**
     * 获得成员按score值递减(从大到小)排列的排名,排名以0为底,score值最小的成员排名为0
     *
     * @param key
     * @param member 成员
     * @return 如果 member是有序集 key 的成员，返回 member 的排名。如果 member 不是有序集 key 的成员，返回 nil 。
     */
    Long zrank( String key, String member );

    /**
     * 获得成员按score值递减(从大到小)排列的排名,排名以0为底,score值最大的成员排名为 0
     *
     * @param key
     * @param member 成员
     * @return 如果 member是有序集 key 的成员，返回 member 的排名。如果 member 不是有序集 key 的成员，返回 nil 。
     */
    Long zrevrank( String key, String member );

    /**
     * 返回有序集 key 中， score值在min和max之间(默认包括score值等 min或max)的成员的数量。
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return score 值在 min 和 max 之间的成员的数量。
     */
    Long zcount( String key, double min, double max );

    /**
     * 返回有序集 key中，指定区间内的成员。其中成员的位置按 score值递增(从小到大)来排序。
     *
     * @param key
     * @param start 开始下标
     * @param end   结束下标
     * @return 指定区间内，有序集成员的列表。
     */
    Set< String > zrange( String key, int start, int end );

    /**
     * 返回有序集 key中，指定区间内的成员。其中成员的位置按 score值递增(从小到大)来排序。
     *
     * @param key
     * @param start 开始下标
     * @param end   结束下标
     * @param claz  成员类型
     * @return 指定区间内，有序集成员的列表。
     */
    < T > Set< T > zrange( String key, int start, int end, Class< T > claz );

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，带有 score值的有序集成员的列表。
     */
    Set< Tuple > zrangeWithScores( String key, int start, int end );

    /**
     * 返回有序集 key 中，指定区间内的成员。其中成员的位置按 score 值递减(从大到小)来排列。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，有序集成员的列表。
     */
    Set< String > zrevrange( String key, int start, int end );

    /**
     * 返回有序集 key 中， score值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     *
     * @param key
     * @param start
     * @param end
     * @return 指定区间内，带有 score值的有序集成员的列表。
     */
    Set< Tuple > zrevrangeWithScores( String key, int start, int end );

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set< String > zrangeByScore( String key, double min, double max );

    Set< Tuple > zrangeByScoreWithScores( String key, double min, double max );

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     *
     * @param key
     * @param max
     * @param min
     * @return 指定区间内，的有序集成员的列表。
     */
    Set< String > zrevrangeByScore( String key, double max, double min );

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     *
     * @param key
     * @param max
     * @param min
     * @return 指定区间内，带有 score 值的有序集成员的列表。
     */
    Set< Tuple > zrevrangeByScoreWithScores( String key, double max, double min );

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key
     * @param member
     * @return 被成功移除的成员的数量，不包括被忽略的成员。
     */
    Boolean zrem( String key, String member );

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     *
     * @param key
     * @param start
     * @param end
     * @return 被移除成员的数量。
     */
    Long zremByScore( String key, double start, double end );

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员。区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
     *
     * @param key
     * @param start 以0开始,以-1表示最后一个成员
     * @param end   以0开始,以-1表示最后一个成员
     * @return 被移除成员的数量
     */
    Long zremByRank( String key, long start, long end );

    /**
     * 返回有序集 key 的基数
     *
     * @param key
     * @return 当 key 存在且是有序集类型时，返回有序集的基数。当 key 不存在时，返回 0 。
     */
    Long zcard( String key );

    /**
     * 发布,订阅继承BaseJedisSubscriber,配置成spring bean服务
     *
     * @param channel 频道
     * @param message 消息
     * @return
     */
    Long publish( String channel, String message );

    /**
     * 设置过期时间
     * 移除过期时间调用方法 @see {@link RedisService#persist(String)}
     *
     * @param key
     * @param seconds 秒
     */
    Long expire( String key, int seconds );

    /**
     * 设置过期时间
     *
     * @param key
     * @param unixTime UNIX时间戳
     */
    Long expireAt( String key, long unixTime );

    /**
     * 以毫秒为单位设置 key的过期 unix时间戳
     *
     * @param key
     * @param millisecondsTimestamp unix时间戳
     */
    Long pexpireat( String key, long millisecondsTimestamp );

    /**
     * 移除过期时间(易失转持久)
     *
     * @param key
     */
    Long persist( String key );

}
