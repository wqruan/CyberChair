package SELab.remote;

import SELab.domain.User;

public class RemoteUser {

    public static User findByUsername(String username){
        return new User();
    }
    public static User findById(long id){
        return new User();
    }
    public static User findByEmail(String email){
        return new User();
    }

    public static User findByFullnameAndEmail(String fullname,String email){
        return new User();
    }
}
