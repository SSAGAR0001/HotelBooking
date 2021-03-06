package com.sagarsaini.landon.business.service;

import com.sagarsaini.landon.business.domain.RoomReservation;
import com.sagarsaini.landon.data.entity.Room;
import com.sagarsaini.landon.data.entity.Guest;
import com.sagarsaini.landon.data.entity.Reservation;
import com.sagarsaini.landon.data.repository.GuestRepository;
import com.sagarsaini.landon.data.repository.ReservationRepository;
import com.sagarsaini.landon.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {
    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date){
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });
        Iterable<Reservation> reservations = this.reservationRepository.findByDate(new java.sql.Date(date.getTime()));
        if(null != reservations){
            reservations.forEach(reservation -> {
                Guest guest = new Guest();
                Optional<Guest> opt = this.guestRepository.findById(reservation.getGuestId());
                if(opt.isPresent()) {
                    guest = opt.get();
                }
                if(null != guest){
                    RoomReservation roomReservation = roomReservationMap.get(reservation.getId());
                    roomReservation.setDate(date);
                    roomReservation.setFirstName(guest.getFirstName());
                    roomReservation.setLastName(guest.getLastName());
                    roomReservation.setGuestId(guest.getId());
                }
            });
        }
//        Optional<Foo> fooOptional = fooRepository.findById(id);
//        if (fooOptional.isPresent()){
//            Foo foo = fooOptional.get();
//            // processing with foo ...
//        }
        List<RoomReservation> roomReservations = new ArrayList<>();
        for(Long roomId:roomReservationMap.keySet()){
            roomReservations.add(roomReservationMap.get(roomId));
        }
        return roomReservations;
    }
}
