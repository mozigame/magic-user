package com.magic.user.storage.util;


import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.tools.MD5Util;
import com.magic.user.enums.AccountStatus;
import com.magic.user.vo.AgentConditionVo;
import com.magic.user.vo.MemberConditionVo;
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
import org.apache.commons.codec.digest.Md5Crypt;
import org.bson.Document;
import org.junit.Test;
import sun.security.rsa.RSASignature;

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
        mongoDatabase = mongoClient.getDatabase("admin");
        collection = mongoDatabase.getCollection("onLineMember");
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
        MongoCursor<Document> cursor1 = collection.find().sort(new Document("userId", 1)).skip(0).limit(10).iterator();
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
        System.out.println(collection.updateMany(eq("userId", 2017050500110033L), new Document("$set", new Document("username", "update2"))));
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

    @Test
    public void addAgentMongo() {
        System.out.println(0x10);
        Long[] ids=new Long[]{11111L,2222L};
        System.out.println(JSONObject.toJSONString(ids));
        Integer a= null;
        int b=a;
        System.out.println(b);
        AgentConditionVo vo=new AgentConditionVo();
        vo.setAgentId(100000L);
        vo.setStatus(AccountStatus.enable.value());
        vo.setAgentName("agent_test");
        vo.setDepositMoney(10000L);
        vo.setWithdrawMoney(10000L);
        vo.setMembers(1000);
        vo.setGeneralizeCode("EDFSEFAS356O");
        vo.setRegisterTime(System.currentTimeMillis());
        System.out.println(JSONObject.toJSONString(vo));
    }

    @Test
    public void md5Test() {
    }
    @Test
    public void addMemberMongo() {
        MemberConditionVo vo=new MemberConditionVo();
        vo.setMemberId(1000000L);
        vo.setMemberName("member_test");
        vo.setAgentId(100000L);
        vo.setAgentName("agent_test");
        vo.setRegisterTime(System.currentTimeMillis());
        vo.setStatus(AccountStatus.enable.value());
        vo.setLevel(0L);
        vo.setDepositCount(1000);
        vo.setWithdrawCount(1000);
        vo.setWithdrawMoney(10000L);
        vo.setDepositMoney(10000L);
        vo.setCurrencyType(1);

        System.out.println(JSONObject.toJSONString(vo));
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

    public static void main(String[] args) {
        Class<?> clazz = MongoDbFactory.class;
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.get(clazz.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod("test");

            // 使用javassist的反射方法的参数名
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                int len = ctMethod.getParameterTypes().length;
                // 非静态的成员函数的第一个参数是this
                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                System.out.print("test : ");
                for (int i = 0; i < len; i++) {
                    System.out.print(attr.variableName(i + pos) + ' ');
                }
                System.out.println();
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void test(String param1, int param2, int parem3) {
        System.out.println(param1 + param2 + parem3);
    }


    public static String[] getAllParamaterName(Method method)
            throws NotFoundException {
        Class<?> clazz = method.getDeclaringClass();
        ClassPool pool = ClassPool.getDefault();
        CtClass clz = pool.get(clazz.getName());
        CtClass[] params = new CtClass[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            params[i] = pool.getCtClass(method.getParameterTypes()[i].getName());
        }
        CtMethod cm = clz.getDeclaredMethod(method.getName(), params);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                .getAttribute(LocalVariableAttribute.tag);
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        String[] paramNames = new String[cm.getParameterTypes().length];
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }

    @Test
    public void methodTest() throws Exception {
        Method method = MongoDbFactory.class.getMethod("test", String.class, int.class, int.class);
        String[] paramaterName = getAllParamaterName(method);
        System.out.println(JSONObject.toJSONString(paramaterName));
//        assertArrayEquals(paramaterName, new String[]{"name"});
    }

    /**
     * 获取方法参数名列表
     *
     * @param clazz
     * @param m
     * @return
     * @throws IOException
     */
//    public static List<String> getMethodParamNames(Class<?> clazz, Method m) throws IOException {
//        try (InputStream in = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class")) {
//            return getMethodParamNames(in,m);
//        }
//
//    }
//    public static List<String> getMethodParamNames(InputStream in, Method m) throws IOException {
//        try (InputStream ins=in) {
//            return getParamNames(ins,
//                    new EnclosingMetadata(m.getName(), Type.getMethodDescriptor(m), m.getParameterTypes().length));
//        }
//
//    }
}
