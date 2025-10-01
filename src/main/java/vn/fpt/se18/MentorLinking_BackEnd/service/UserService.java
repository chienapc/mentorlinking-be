package vn.fpt.se18.MentorLinking_BackEnd.service;

import java.util.List;
import vn.fpt.se18.MentorLinking_BackEnd.entity.User;

public interface UserService {

    public User create(User user);
    public User update(User user);
    public User delete(User user);
    public User findById(int id);
    public List<User> findAll();

}
