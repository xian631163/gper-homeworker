/**
 * 
 */
package proxy;

import java.lang.reflect.Method;

/**
 * @author YFZX-zsx
 */
public interface ZsxInvocationhandler {

  public Object invoke(Object arg0, Method arg1, Object[] arg2)
      throws Throwable;

}
