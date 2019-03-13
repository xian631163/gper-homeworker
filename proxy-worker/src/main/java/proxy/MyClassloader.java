/**
 * 
 */
package proxy;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @author YFZX-WB
 * @2019年3月13日
 */
public class MyClassloader extends ClassLoader {

  private static final String ln = "\r\n";

  public MyClassloader() {
    super();

  }

  public final Class<?> getMyClass(Class<?>[] interfaces, ZsxInvocationhandler h)
      throws IOException, ClassNotFoundException {
    Class<?> clazz = null;
    // 1. 生成源代码
    String src = generateSrc(interfaces, h);

    System.out.println(src);
    // 2. 将源码写入磁盘
    String path = interfaces[0].getClass().getResource("/").toString();
    String filePath = path + "$proxy0.java";
    try {
      PrintWriter pt = new PrintWriter(new FileWriter(filePath));

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

    int res = compiler.run(null, null, null, filePath);

    if (res == 1) {
      clazz = loadClass("$proxy0");
    }
    else {
      return null;
    }

    return clazz;
  }

  private String generateSrc(Class<?>[] interfaces, ZsxInvocationhandler h) {
    Class<?> clazz = interfaces[0];
    Set<String> strSet = new HashSet<String>();
    strSet.add("equals");
    strSet.add("toString");
    strSet.add("hashCode");
    Method[] methods = clazz.getMethods();
    StringBuffer sb = new StringBuffer();
    sb.append("package proxy;" + ln).append("import java.lang.reflect *;" + ln);
    sb.append("public class $proxy0 implements " + clazz.getName() + "{" + ln);
    sb.append("   private ZsxInvocationhandler h;" + ln);
    sb.append("   public $proxy0(){" + ln);
    sb.append("      h = new  " + h.getClass().getName() + "();" + ln);
    sb.append("}" + ln);
    for (Method m : methods) {
      String retype = m.getReturnType().getSimpleName();
      if (!strSet.contains(m.getName())) {
        sb.append(" public " + retype + " " + m.getName() + "( ){ " + ln);
        sb.append("try{" + ln);
        sb.append("    h.invoke(null,m,m.getParameters());" + ln);
        sb.append("}catch (Throwable e){" + ln);
        sb.append("    e.printStackTrace();" + ln);
        sb.append("}" + ln);

        sb.append("}" + ln);
      }
    }
    sb.append("}" + ln);

    return sb.toString();
  }

}
