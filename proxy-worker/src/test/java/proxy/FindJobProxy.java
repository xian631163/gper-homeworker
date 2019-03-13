/**
 * 
 */
package proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author YFZX-WB
 * @2019��3��13��
 */
public class FindJobProxy implements ZsxInvocationhandler {

  private Object target;

  /**
   * 
   */
  public FindJobProxy() {
    // TODO Auto-generated constructor stub

  }

  public Object getInstance(IFindJob job) {

    this.target = job;

    Object object = null;

    try {
      object = ZsxProxy.newProxyInstance(new MyClassloader(), job.getClass()
          .getInterfaces(), this);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return object;

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
   * java.lang.reflect.Method, java.lang.Object[])
   */
  public Object invoke(Object arg0, Method arg1, Object[] arg2)
      throws Throwable {
    // TODO Auto-generated method stub
    befor();
    arg1.invoke(target, arg2);
    after();
    return null;
  }

  private void befor() {
    System.out.println("�������Ƿ���ְλ�ܴ����̣�");
  }

  private void after() {
    System.out.println("�������ְλ�ѷ����ɹ���");
  }

}
