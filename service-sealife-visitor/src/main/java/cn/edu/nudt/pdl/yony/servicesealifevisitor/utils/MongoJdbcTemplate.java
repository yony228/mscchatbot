package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * email: yony228@163.com
 * Created by yony on 18-1-4.
 */
@Component
public class MongoJdbcTemplate {
        @Autowired
        private MongoDataSource mongoDataSource;
        private MongoClient mongoClient;

        // constructor
        public MongoJdbcTemplate() {
                this.mongoClient = mongoDataSource.getClient();
        }

        // constructor with mongoDataSource
        public MongoJdbcTemplate(MongoDataSource mongoDataSource) {
                this.mongoDataSource = mongoDataSource;
                this.mongoClient = this.mongoDataSource.getClient();
        }

        /**
         * Get collection using default database;
         * @param collection Collection name
         * @return MongoCollection
         */
        public MongoCollection<Document> getCollection(String collection) {
                MongoDatabase db = this.mongoClient.getDatabase(this.mongoDataSource.database);
                return db.getCollection(collection);
        }

        /**
         * Insert one document map formatted to collection
         * @param collection
         * @param map
         */
        public void addMapObject(String collection, Map<String, Object> map) {
                if(MapUtils.isEmpty(map)) {
                        return;
                }
                Document doc = new Document();
                doc.putAll(map);
                getCollection(collection).insertOne(doc);
        }

        /**
         * select document by id which is ObjectId formed
         * @param collection
         * @param id
         * @return
         */
        public Document findObjectById(String collection, String id) {
                ObjectId _id = null;
                try {
                        _id = new ObjectId(id);
                } catch (Exception e) {
                        return null;
                }
                Document doc = getCollection(collection).find(Filters.eq("_id", _id)).first();
                return doc;
        }

        /**
         * select object by one param key and value
         * @param collection
         * @param param
         * @param value
         * @return
         */
        public Document findObjectByParam(String collection, String param, Object value) {
                return getCollection(collection).find(Filters.eq(param, value)).first();
        }

}
