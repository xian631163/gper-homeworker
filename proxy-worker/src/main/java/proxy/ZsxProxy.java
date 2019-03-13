/**
 * 
 */
package proxy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author YFZX-WB
 * @2019Äê3ÔÂ13ÈÕ
 */
public class ZsxProxy {

  /*
   * (non-Javadoc)
   * 
   * @see proxy.ZsxInvocationhandler#invoke()
   */

  public ZsxProxy() {
  }

  public static Object newProxyInstance(MyClassloader loader,
      Class<?>[] interfaces, ZsxInvocationhandler h) {
    Object object = null;

    try {
      Class<?> clazz = loader.getMyClass(interfaces, h);
      object = clazz.getConstructor().newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return object;
  }

}
