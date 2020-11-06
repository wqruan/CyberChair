package testFind; /**
 * @author : wqruan
 * @version : 1.0.0
 * @date : 2020/11/3 21:58
 */

import SELab.domain.Article;
import SELab.domain.User;
import SELab.remote.RemoteArticle;
import SELab.remote.RemoteUser;
import org.junit.Test;

import java.util.List;


public class testFindbyUserID {
    @Test
    public void testFindbyID()throws Exception{
        User tmp1 = RemoteUser.findById(1);
        List<Article> tmp= RemoteArticle.findByMeetingNameAndStatus("wqruan","asdfaf");

    }
}
