package com.gym.gym.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.gym.domain.Option;
import com.gym.gym.domain.Page;
import com.gym.gym.domain.Reservation;
import com.gym.gym.mapper.ReservationMapper;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    // 예약 등록
    @Override
    public int insert(Reservation reservation) throws Exception {
        int result = reservationMapper.insert(reservation);
        return result;
    }

    // 예약 목록
    @Override
    public List<Reservation> list(Option option, Page page) throws Exception {
        int total = count(option);
        page.setTotal(total);
        List<Reservation> reservationList = reservationMapper.list(option, page);
        return reservationList;
    }

    // 예약 조회
    @Override
    public Reservation read(int no) throws Exception {
        Reservation reservation = reservationMapper.read(no);
        return reservation;
    }

    // 예약 취소(수정)
    @Override
    public int cancel(Reservation reservation) throws Exception {
        int result = reservationMapper.cancel(reservation);
        return result;
    }

    @Override
    public int count(Option option) throws Exception {
        return reservationMapper.count(option);
    }

}