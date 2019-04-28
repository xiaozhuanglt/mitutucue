import com.xiaozhuanglt.mitutucue.controller.DemoController;
import com.xiaozhuanglt.mitutucue.service.interfaces.ServiceDemo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 测试类框架测试
 * @author: hxz
 * @create: 2019-04-28 16:22
 **/
public class TestDemo extends BaseTest{

    @Autowired
    ServiceDemo serviceDemo;

    @Test
    public void test1(){

        try {
            Long areaId = Long.valueOf(4);
            int i = serviceDemo.queryArea(areaId);
            System.out.print(i);
        }catch (Exception e){
            System.out.println(e);
        }
    }

}
