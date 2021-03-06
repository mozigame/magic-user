package com.magic.user.storage.util;


import com.alibaba.fastjson.JSONObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.bson.Document;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbFactory {

    public static MongoDatabase mongoDatabase = null;
    public static MongoCollection<Document> collection = null;

    static {
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://202.153.207.179:8999,202.153.207.181:8999,202.153.207.182:8999");
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        mongoDatabase = mongoClient.getDatabase("test8");
        collection = mongoDatabase.getCollection("user_test");
    }

    @Test
    public void add() {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Document document = new Document();
            document.append("userId", 2017050500110031L + i)
                    .append("realname", "joey" + i)
                    .append("username", "ju" + i)
                    .append("status", 1)
                    .append("diaplay", 1)
                    .append("agentId", 1000);
            documents.add(document);
        }
        for (int i = 0; i < 100; i++) {
            Document document = new Document();
            document.append("userId", 2017050500110031L + i)
                    .append("realname", "joey" + i)
                    .append("username", "ju" + i)
                    .append("status", 1)
                    .append("diaplay", 1)
                    .append("agentId", 1001);
            documents.add(document);
        }
        collection.insertMany(documents);
    }

    @Test
    public void findCount() {
        System.out.println(collection.count(eq("agentId", 1000)));
        System.out.println(collection.count());
    }

    @Test
    public void findFirst() {
        System.out.println(collection.find().first().toJson());
    }


    @Test
    public void findByPage() {
        MongoCursor<Document> cursor = collection.find().sort(new Document("userId", -1)).skip(0).limit(10).iterator();
        MongoCursor<Document> cursor1 = collection.find().sort(new Document("userId", 1)).skip(5).limit(10).iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
        while (cursor1.hasNext()) {
            System.out.println(cursor1.next().toJson());
        }
    }

    @Test
    public void findAll() {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
    }

    @Test
    public void findOneByFilter() {
        Document myDoc = null;
        myDoc = collection.find(new Document("realname", new Document("$eq", "joey77"))).first();
        System.out.println(myDoc.toJson());
    }

    @Test
    public void findMoreByFilter() {
        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        };

        collection.find(eq("agentId", 1000)).forEach(printBlock);

//        collection.find(
//                new Document("stars", new Document("$gte", 2)
//                        .append("$lt", 5))
//                        .append("categories", "Bakery")).forEach(printBlock);
    }

    @Test
    public void updateOne() {
        System.out.println(collection.updateOne(eq("userId", 2017050500110033L), new Document("$set", new Document("username", "update2"))));
    }

    @Test
    public void deleteOne() {
        System.out.println(collection.deleteOne(eq("userId", 2017050500110033L)));
    }

    @Test
    public void deleteMany() {
        collection.deleteMany(eq("agentId", 1000));
    }

    @Test
    public void createIndex() {
        collection.createIndex(new Document("userId", 1));
    }


    public void getParam(Object... args) {
        System.out.println(args.length);
    }

    @Test
    public void getParamLength() {
        int a = 1;
        int b = 2;
        int c = 3;
        getParam(a, b, c);
    }

}
