package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.dto.RegisterRequest;
import BookingHomeStay.BookingHomeStay.entity.User;

public interface UserService {
    User register(RegisterRequest request);
}