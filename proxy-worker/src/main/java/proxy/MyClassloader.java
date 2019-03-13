/**
 * 
 */
package proxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


/**
 * @author YFZX-WB
 * @2019年3月13日
 */
public class MyClassloader extends ClassLoader {

  private static final String ln = "\r\n";
  private File classPathFile;

  public MyClassloader() {
    String classPath = MyClassloader.class.getResource("").getPath();
    this.classPathFile = new File(classPath);

  }

  public final Class<?> getMyClass(Class<?>[] interfaces, ZsxInvocationhandler h)
      throws IOException, ClassNotFoundException {
    Class<?> clazz = null;
    // 1. 生成源代码
    String src = generateSrc(interfaces, h);

    System.out.println(src);
    // 2. 将源码写入磁盘
    String path = MyClassloader.class.getResource("").getPath();
    String filePath = path + "$proxy0.java";
    File f =null;
    try {
       f = new File(filePath);
      PrintWriter pt = new PrintWriter(f);

      pt.print(src);
      pt.flush();
      pt.close();
    }
    catch (FileNotFoundException e) {

      e.printStackTrace();
    }
    System.out.println(filePath);
    // 3. 编译 并加载
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    StandardJavaFileManager manage = compiler.getStandardFileManager(null,null,null);
    Iterable iterable = manage.getJavaFileObjects(f);

   JavaCompiler.CompilationTask task = compiler.getTask(null,manage,null,null,null,iterable);
   task.call();
   manage.close();
    return findClass("$proxy0");
  }

  private String generateSrc(Class<?>[] interfaces, ZsxInvocationhandler h) {
    Class<?> clazz = interfaces[0];
    Method[] methods = clazz.getMethods();
    StringBuffer sb = new StringBuffer();
    sb.append("package proxy;" + ln).append("import java.lang.reflect *;" + ln);
    sb.append("public class $proxy0 implements " + clazz.getName() + "{" + ln);
    sb.append("   private ZsxInvocationhandler h;" + ln);
    sb.append("   public $proxy0(){" + ln);
    sb.append("      h = new  " + h.getClass().getName() + "();" + ln);
    sb.append("}" + ln);
    for (Method m : methods) {
      Class<?>[] params = m.getParameterTypes();
      StringBuffer paramNames = new StringBuffer();
      StringBuffer paramValues = new StringBuffer();
      StringBuffer paramClasses = new StringBuffer();
      
      for (int i = 0; i < params.length; i++) {
        Class<?> clazz1 = params[i];
        String type = clazz1.getName();
        String paramName = toLowerFirstCase(clazz1.getSimpleName());
        paramNames.append(type + " " +  paramName);
        paramValues.append(paramName);
        paramClasses.append(clazz1.getName() + ".class");
        if(i > 0 && i < params.length-1){
            paramNames.append(",");
            paramClasses.append(",");
            paramValues.append(",");
        }
    }
//      if (!strSet.contains(m.getName())) {
        sb.append("public " + m.getReturnType().getName() + " " + m.getName() + "(" + paramNames.toString() + ") {" + ln);
        sb.append("try{" + ln);
        sb.append("Method m = " + interfaces[0].getName() + ".class.getMethod(\"" + m.getName() + "\",new Class[]{" + paramClasses.toString() + "});" + ln);
        sb.append((hasReturnValue(m.getReturnType()) ? "return " : "") + getCaseCode("this.h.invoke(this,m,new Object[]{" + paramValues.toString() + "})",m.getReturnType()) + ";" + ln);
        sb.append("}catch(Error _ex) { }");
        sb.append("catch(Throwable e){" + ln);
        sb.append("throw new UndeclaredThrowableException(e);" + ln);
        sb.append("}");
        sb.append(getReturnEmptyCode(m.getReturnType()));
        sb.append("}");
//      }
    }
    sb.append("}" + ln);

    return sb.toString();
  }
  
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {

      String className = MyClassloader.class.getPackage().getName() + "." + name;
      if(classPathFile  != null){
          File classFile = new File(classPathFile,name.replaceAll("\\.","/") + ".class");
          if(classFile.exists()){
              FileInputStream in = null;
              ByteArrayOutputStream out = null;
              try{
                  in = new FileInputStream(classFile);
                  out = new ByteArrayOutputStream();
                  byte [] buff = new byte[1024];
                  int len;
                  while ((len = in.read(buff)) != -1){
                      out.write(buff,0,len);
                  }
                  return defineClass(className,out.toByteArray(),0,out.size());
              }catch (Exception e){
                  e.printStackTrace();
              }
          }
      }
      return null;
  }
  
  private static Map<Class,Class> mappings = new HashMap<Class, Class>();
  static {
      mappings.put(int.class,Integer.class);
  }

  private  String getReturnEmptyCode(Class<?> returnClass){
      if(mappings.containsKey(returnClass)){
          return "return 0;";
      }else if(returnClass == void.class){
          return "";
      }else {
          return "return null;";
      }
  }

  private  String getCaseCode(String code,Class<?> returnClass){
      if(mappings.containsKey(returnClass)){
          return "((" + mappings.get(returnClass).getName() +  ")" + code + ")." + returnClass.getSimpleName() + "Value()";
      }
      return code;
  }

  private  boolean hasReturnValue(Class<?> clazz){
      return clazz != void.class;
  }
  
  private  String toLowerFirstCase(String src){
    char [] chars = src.toCharArray();
    chars[0] += 32;
    return String.valueOf(chars);
}


}
