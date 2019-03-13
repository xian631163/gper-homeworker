/**
 * 
 */
package proxy;

import java.lang.reflect.Method;

/**
 * @author YFZX-zsx
 * @2019Äê3ÔÂ13ÈÕ
 */
public class ProxyTest {

  /**
   * @param args
   */
  public static  void main(String[] args) {
    // TODO Auto-generated method stub

    FindJobProxy proxy = new FindJobProxy();

    IFindJob job = (IFindJob) proxy.getInstance(new FindJob51());
    job.findJob();
    Method[] methods = job.getClass().getDeclaredMethods();
    for (Method m : methods) {
      String retype = m.getName();
      System.out.println(job.getClass().getName());
      try {
       
      }
      catch (Throwable e) {
        // TODO: handle exception
        e.printStackTrace();
      }
    }
  }

}
