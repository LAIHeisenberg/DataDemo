package com.longmai.datademo.interceptor;

import javassist.*;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class JavassistInjector extends Thread{

    public static void premain(String args, Instrumentation instrumentation0){

        instrumentation = instrumentation0;
    }

    public static void agentmain(String args, Instrumentation instrumentation0){

        instrumentation = instrumentation0;
    }

    public void run(){
        injectMySQL();
    }

    public static CtClass[] getMethodParam(String methodsign, ClassPool pool)
    {
        int sinx = methodsign.indexOf("(") + 1;
        int einx = methodsign.indexOf(")");
        String str = methodsign.substring(sinx, einx);

        if(str!=null && !"".equals(str.trim())){
            String[] arr = str.split(",");
            CtClass[] param = new CtClass[arr.length];
            for(int i = 0; i < param.length; i++){
                try {
                    param[i] = pool.get(arr[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return param;
        }
        return new CtClass[0];
    }
    static Instrumentation instrumentation = null;
    public Class findClass(String className){
        Class[] classes = instrumentation.getAllLoadedClasses();
        for(Class c : classes){
            if(c.getName().equals(className)){
                return c;
            }
        }
        return null;
    }

    public Class findClassInterval(String className){
        Class cls = null;
        for(int i=0;i<5;i++){
            cls = findClass(className);
            if(cls != null){
                return cls;
            }
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        return null;
    }

    public  void injectMySQL(){
        ClassPool pool = ClassPool.getDefault();
        Class nsessionClass = findClassInterval("com.mysql.cj.NativeSession");
        ClassDefinition classDefinition = null;
        try{
            pool.insertClassPath(new ClassClassPath(nsessionClass));
            Class mysqlHandlerClass = nsessionClass.getClassLoader().loadClass("com.longmai.datademo.interceptor.MySQLSessionInjectHandler");

            CtClass ctClass = pool.get("com.mysql.cj.NativeSession");
            if(ctClass.isFrozen()){
                ctClass.defrost();
            }

            CtMethod cm = null;
            try{
                //此时对应mysql的版本为 mysql-connector-java-8.0.19.jar
                cm = ctClass.getDeclaredMethod("execSQL",
                        getMethodParam("public com.mysql.cj.protocol.Resultset com.mysql.cj.NativeSession" +
                                ".execSQL(com.mysql.cj.Query,java.lang.String,int,com.mysql.cj.protocol.a.NativePacketPayload,boolean,com.mysql.cj.protocol.ProtocolEntityFactory,com.mysql.cj.protocol.ColumnDefinition,boolean)",pool));
            }catch(Throwable tw){
                tw.printStackTrace();
            }

//            cm.insertBefore("$2=com.longmai.datademo.interceptor.MySQLSessionInjectHandler.getSQL2(this,$1,$2);");
//            cm.insertBefore("{System.out.println(\"ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd($2)\");}");
            //cm.insertBefore("com.longmai.datademo.interceptor.MySQLSessionInjectHandler.getSQL(this,$1,$2);");
            byte[] buffer = ctClass.toBytecode();
            if(ctClass.isFrozen()){
                ctClass.defrost();
            }
            classDefinition = new ClassDefinition(nsessionClass,buffer);

            instrumentation.redefineClasses(classDefinition);
        }catch(Throwable tw){
            tw.printStackTrace();
        }
    }


}
