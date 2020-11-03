/**
 * @author : wqruan
 * @version : 1.0.0
 * @date : 2020/11/3 21:58
 */
import MainService.Service.ArticleService;
import MainService.domain.User;
import org.junit.Test;
public class testFindbyUserID {
    @Test
    public void testFindbyID(){
        User user = ArticleService.findById(1);

    }
}
