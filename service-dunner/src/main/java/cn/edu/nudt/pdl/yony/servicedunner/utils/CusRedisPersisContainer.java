package cn.edu.nudt.pdl.yony.servicedunner.utils;

import cn.yony.automaton.simple.persistence.RedisPersisContainer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.HashMap;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-13.
 */
@Component
public class CusRedisPersisContainer<kt, vt> extends RedisPersisContainer<kt, vt>{

        private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        private JedisPool pool;
        private Jedis jedis = null;
        private static int EX_TIME = 3600;

        public CusRedisPersisContainer() {
                this.jedisPoolConfig.setMaxTotal(10);
                this.pool = new JedisPool(this.jedisPoolConfig, "localhost", 6379);
        }

        public CusRedisPersisContainer(int max_num, String host, int port, int ex_time) {
                this.jedisPoolConfig.setMaxTotal(max_num);
                this.pool = new JedisPool(this.jedisPoolConfig, host, port);
                CusRedisPersisContainer.EX_TIME = ex_time;
        }

        @Override
        public void put(kt key, vt val) {
                jedis = pool.getResource();
                try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(val);
                        byte[] bytes = baos.toByteArray();
                        jedis.setex(key.toString().getBytes(), EX_TIME, bytes);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                jedis.close();
        }

        @Override
        public vt get(kt key) {
                jedis = pool.getResource();
                vt tmp = null;
                try {
                        ByteArrayInputStream bais = null;
                        bais = new ByteArrayInputStream(jedis.get(key.toString().getBytes()));
                        ObjectInputStream ois = null;
                        ois = new ObjectInputStream(bais);
                        tmp = (vt)ois.readObject();
                } catch (IOException e) {
                        e.printStackTrace();
                } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                } finally {
                        jedis.close();
                        return tmp;
                }
        }

        @Override
        public void remove(kt key) {
                jedis = pool.getResource();
                jedis.del(key.toString());
                jedis.close();
        }

        @Override
        public boolean containsKey(kt key) {
                jedis = pool.getResource();
                boolean rtVal = jedis.exists(key.toString());
                jedis.close();
                return rtVal;
        }

        @Override
        public void putAll(HashMap<kt, vt> map) {
                jedis = pool.getResource();
                for (kt key : map.keySet()) {
                        this.put(key, map.get(key));
                }
                jedis.close();
        }
}
