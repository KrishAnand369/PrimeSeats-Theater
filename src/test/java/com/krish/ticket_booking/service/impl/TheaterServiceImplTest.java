package com.krish.ticket_booking.service.impl;

import com.krish.ticket_booking.dto.request.TheaterRegisterRequest;
import com.krish.ticket_booking.dto.response.TheaterResponseDto;
import com.krish.ticket_booking.entity.Theater;
import com.krish.ticket_booking.entity.User;
import com.krish.ticket_booking.entity.enums.RoleEnum;
import com.krish.ticket_booking.exception.TheaterNotFoundException;
import com.krish.ticket_booking.exception.UserNotFoundException;
import com.krish.ticket_booking.mapper.TheaterMapper;
import com.krish.ticket_booking.repository.TheaterRepository;
import com.krish.ticket_booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheaterServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private TheaterMapper theaterMapper;

    @Mock
    private Authentication authentication;

    @Captor
    private ArgumentCaptor<Theater> theaterCaptor;

    @InjectMocks
    private TheaterServiceImpl theaterService;

    @Test
    void createTheater_ShouldCreateTheater_Successfully() {
        //Arrange

        UUID managerId = UUID.randomUUID();

        TheaterRegisterRequest theaterRegisterRequest = new TheaterRegisterRequest(
                "Jose Cinemas",
                "Mananthavady",
                managerId
        );

        User mockManager = User.builder()
                .id(managerId)
                .email("manager@example.com")
                .role(RoleEnum.MANAGER)  // ⚠️ CRITICAL: Must be MANAGER
                .build();

        Theater savedTheater = Theater.builder()
                .id(UUID.randomUUID())  // Simulate DB-generated ID
                .name("Jose Cinemas")
                .location("Mananthavady")
                .manager(mockManager)
                .build();

        // 4. Create the expected response DTO
        TheaterResponseDto expectedResponse = new TheaterResponseDto(
                savedTheater.getId(),
                savedTheater.getName(),
                savedTheater.getLocation()
        );

        when(userRepository.findById(managerId)).thenReturn(Optional.of(mockManager));
        when(theaterRepository.save(any(Theater.class))).thenReturn(savedTheater);
        when(theaterMapper.toDto(savedTheater)).thenReturn(expectedResponse);

        // --- ACT ---
        TheaterResponseDto result = theaterService.createTheater(theaterRegisterRequest);

        //ASSERT
        assertNotNull(result);
        assertEquals(result.name(),theaterRegisterRequest.name());
        assertEquals(result.location(),theaterRegisterRequest.location());
        assertEquals(result.id(),expectedResponse.id());

        // 2. Verify interactions
        verify(userRepository).findById(managerId);
        verify(theaterRepository).save(any(Theater.class));
        verify(theaterMapper).toDto(savedTheater);

    }

    @Test
    void createTheater_ShouldThrowException_WhenManagerWithIdNotExist (){

        UUID managerId = UUID.randomUUID();

        TheaterRegisterRequest theaterRegisterRequest = new TheaterRegisterRequest(
                "Jose Cinemas",
                "Mananthavady",
                managerId
        );
        when(userRepository.findById(managerId)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> theaterService.createTheater(theaterRegisterRequest));

        assertEquals("Manager not found",userNotFoundException.getMessage());

        verify(userRepository).findById(managerId);
        verify(theaterRepository,never()).save(any(Theater.class));
        verify(theaterMapper,never()).toDto(any(Theater.class));
    }

    @Test
    void createTheater_ShouldThrowException_WhenUserIsNotManager(){
        UUID managerId = UUID.randomUUID();

        TheaterRegisterRequest request = new TheaterRegisterRequest(
                "Jose Cinemas",
                "Mananthavady",
                managerId
        );

        User user = User.builder()
                .id(managerId)
                .name("hari")
                .email("hari@nonmamanger.com")
                .role(RoleEnum.USER)
                .build();

        when(userRepository.findById(managerId)).thenReturn(Optional.of(user));

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> theaterService.createTheater(request));

        assertEquals("Assigned user is not a manager",runtimeException.getMessage());

        verify(userRepository).findById(managerId);
        verify(theaterRepository,never()).save(any(Theater.class));
        verify(theaterMapper,never()).toDto(any(Theater.class));
    }

    @Test
    void getListOfTheaters_ShouldReturnListOfTheaters_SuccessfullyWhenLocationGiven() {

        //Arrange
        String location = "Mananthavady";
        Theater theater1 = Theater.builder()
                .name("Jose Cinemas")
                .location("MaNanthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater1Dto = new TheaterResponseDto(
                theater1.getId(),
                theater1.getName(),
                theater1.getLocation()
        );

        Theater theater2 = Theater.builder()
                .name("Maruthi E Cinemas")
                .location("Mananthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater2Dto = new TheaterResponseDto(
                theater2.getId(),
                theater2.getName(),
                theater2.getLocation()
        );

        Theater theater3 = Theater.builder()
                .name("Jaithra")
                .location("Kalpetta")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater3Dto = new TheaterResponseDto(
                theater3.getId(),
                theater3.getName(),
                theater3.getLocation()
        );

        List<Theater> theaterList = List.of(theater2,theater1);

        when(theaterRepository.findByLocationIgnoreCase(location)).thenReturn(theaterList);
        when(theaterMapper.toDto(theater1)).thenReturn(theater1Dto);
        when(theaterMapper.toDto(theater2)).thenReturn(theater2Dto);

        //Act

        List<TheaterResponseDto> listOfTheaters = theaterService.getListOfTheaters(location);

        //Assert
        assertNotNull(listOfTheaters);
        assertEquals(2,listOfTheaters.size());
        assertTrue(listOfTheaters.contains(theater1Dto));
        assertTrue(listOfTheaters.contains(theater2Dto));
        assertFalse(listOfTheaters.contains(theater3Dto));

        verify(theaterRepository).findByLocationIgnoreCase(location);
        verify(theaterRepository,never()).findAll();
        verify(theaterMapper).toDto(theater1);
        verify(theaterMapper).toDto(theater2);
        verify(theaterMapper,never()).toDto(theater3);

    }

    @Test
    void getListOfTheaters_ShouldReturnListOfTheaters_SuccessfullyWhenLocationNotGiven() {

        Theater theater1 = Theater.builder()
                .name("Jose Cinemas")
                .location("MaNanthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater1Dto = new TheaterResponseDto(
                theater1.getId(),
                theater1.getName(),
                theater1.getLocation()
        );

        Theater theater2 = Theater.builder()
                .name("Maruthi E Cinemas")
                .location("Mananthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater2Dto = new TheaterResponseDto(
                theater2.getId(),
                theater2.getName(),
                theater2.getLocation()
        );

        Theater theater3 = Theater.builder()
                .name("Jaithra")
                .location("Kalpetta")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater3Dto = new TheaterResponseDto(
                theater3.getId(),
                theater3.getName(),
                theater3.getLocation()
        );

        when(theaterRepository.findAll()).thenReturn(List.of(theater3,theater2,theater1));
        when(theaterMapper.toDto(theater1)).thenReturn(theater1Dto);
        when(theaterMapper.toDto(theater2)).thenReturn(theater2Dto);
        when(theaterMapper.toDto(theater3)).thenReturn(theater3Dto);

        //Act
        List<TheaterResponseDto> listOfTheaters = theaterService.getListOfTheaters("");

        //Assert
        assertNotNull(listOfTheaters);
        assertEquals(3,listOfTheaters.size());
        assertTrue(listOfTheaters.contains(theater1Dto));
        assertTrue(listOfTheaters.contains(theater2Dto));
        assertTrue(listOfTheaters.contains(theater3Dto));

        verify(theaterRepository).findAll();
        verify(theaterRepository,never()).findByLocationIgnoreCase(any());
        verify(theaterMapper).toDto(theater1);
        verify(theaterMapper).toDto(theater2);
        verify(theaterMapper).toDto(theater3);
    }

    @Test
    void getListOfTheaters_ShouldReturnListOfTheaters_SuccessfullyWhenLocationIsNull() {

        Theater theater1 = Theater.builder()
                .name("Jose Cinemas")
                .location("Mananthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater1Dto = new TheaterResponseDto(
                theater1.getId(),
                theater1.getName(),
                theater1.getLocation()
        );

        Theater theater2 = Theater.builder()
                .name("Maruthi E Cinemas")
                .location("Mananthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater2Dto = new TheaterResponseDto(
                theater2.getId(),
                theater2.getName(),
                theater2.getLocation()
        );

        Theater theater3 = Theater.builder()
                .name("Jaithra")
                .location("Kalpetta")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater3Dto = new TheaterResponseDto(
                theater3.getId(),
                theater3.getName(),
                theater3.getLocation()
        );

        when(theaterRepository.findAll()).thenReturn(List.of(theater3,theater2,theater1));
        when(theaterMapper.toDto(theater1)).thenReturn(theater1Dto);
        when(theaterMapper.toDto(theater2)).thenReturn(theater2Dto);
        when(theaterMapper.toDto(theater3)).thenReturn(theater3Dto);

        //Act
        List<TheaterResponseDto> listOfTheaters = theaterService.getListOfTheaters(null);

        //Assert
        assertNotNull(listOfTheaters);
        assertEquals(3,listOfTheaters.size());
        assertTrue(listOfTheaters.contains(theater1Dto));
        assertTrue(listOfTheaters.contains(theater2Dto));
        assertTrue(listOfTheaters.contains(theater3Dto));

        verify(theaterRepository).findAll();
        verify(theaterRepository,never()).findByLocationIgnoreCase(any());
        verify(theaterMapper).toDto(theater1);
        verify(theaterMapper).toDto(theater2);
        verify(theaterMapper).toDto(theater3);
    }

    @Test
    void assignTheaterManager_ShouldAssignManager_Successfully() {
     UUID theaterId = UUID.randomUUID();
     UUID userId = UUID.randomUUID();

     User manager = User.builder()
             .name("Manager")
             .id(userId)
             .role(RoleEnum.MANAGER)
             .email("manager@example.com")
             .build();
     Theater theater = Theater.builder()
             .location("Mananthavady")
             .id(theaterId)
             .name("Jose")
             .build();

    when(theaterRepository.findById(theaterId)).thenReturn(Optional.of(theater));
    when(userRepository.findById(userId)).thenReturn(Optional.of(manager));

    //ACT
    theaterService.assignTheaterManager(theaterId,userId);

    verify(theaterRepository).findById(theaterId);
    verify(userRepository).findById(userId);
    verify(theaterRepository).save(theaterCaptor.capture()); //captures


    Theater savedTheater = theaterCaptor.getValue();
    assertNotNull(savedTheater.getManager());
    assertEquals(userId, savedTheater.getManager().getId());
    assertEquals("manager@example.com", savedTheater.getManager().getEmail());

    }

    @Test
    void assignTheaterManager_ShouldThrowUserNotFoundException_WhenUserNotExist() {
        UUID userId = UUID.randomUUID();
        UUID theaterId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> theaterService.assignTheaterManager(theaterId, userId));

    assertEquals("User not Found with id: " + userId,userNotFoundException.getMessage());
    verify(userRepository).findById(userId);
    verify(theaterRepository,never()).save(any());
    verify(theaterRepository,never()).findById(any());
    }

    @Test
    void assignTheaterManager_ShouldThrowTheaterNotFoundException_WhenTheaterNotExist() {

        UUID theaterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .name("manager")
                .id(userId)
                .email("manager@example.com")
                .role(RoleEnum.MANAGER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(theaterRepository.findById(theaterId)).thenReturn(Optional.empty());

        TheaterNotFoundException theaterNotFoundException = assertThrows(TheaterNotFoundException.class, () -> theaterService.assignTheaterManager(theaterId, userId));

        assertEquals("Theater not found with id: "+theaterId,theaterNotFoundException.getMessage());
        verify(theaterRepository).findById(theaterId);
        verify(userRepository).findById(userId);
        verify(theaterRepository,never()).save(any());
    }

    @Test
    void assignTheaterManager_ShouldThrowRunTimeException_WhenUserIsNotManager() {

        UUID theaterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .name("manager")
                .id(userId)
                .email("manager@example.com")
                .role(RoleEnum.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> theaterService.assignTheaterManager(theaterId, userId));

        assertEquals("Assigned user is not a manager",runtimeException.getMessage());

        verify(userRepository).findById(userId);
        verify(theaterRepository,never()).findById(any());
        verify(theaterRepository,never()).save(any());

    }

    @Test
    void getListOfTheatersByManager_Successfully() {
        UUID managerId= UUID.randomUUID();
        String managerEmail = "hari@mail.com";
        User user = User.builder()
                .role(RoleEnum.MANAGER)
                .name("hari")
                .email("hari@mail.com")
                .id(managerId)
                .build();

        Theater theater1 = Theater.builder()
                .name("Jose Cinemas")
                .location("MaNanthavady")
                .id(UUID.randomUUID())
                .manager(user)
                .build();
        TheaterResponseDto theater1Dto = new TheaterResponseDto(
                theater1.getId(),
                theater1.getName(),
                theater1.getLocation()
        );

        Theater theater2 = Theater.builder()
                .name("Maruthi E Cinemas")
                .location("Mananthavady")
                .id(UUID.randomUUID())
                .build();
        TheaterResponseDto theater2Dto = new TheaterResponseDto(
                theater2.getId(),
                theater2.getName(),
                theater2.getLocation()
        );

        Theater theater3 = Theater.builder()
                .name("Jaithra")
                .location("Kalpetta")
                .id(UUID.randomUUID())
                .manager(user)
                .build();
        TheaterResponseDto theater3Dto = new TheaterResponseDto(
                theater3.getId(),
                theater3.getName(),
                theater3.getLocation()
        );

        when(authentication.getName()).thenReturn(managerEmail);
        when(userRepository.findByEmail(managerEmail)).thenReturn(Optional.of(user));
        when(theaterRepository.findByManagerId(managerId)).thenReturn(List.of(theater3,theater1));
        when(theaterMapper.toDto(theater1)).thenReturn(theater1Dto);
        when(theaterMapper.toDto(theater3)).thenReturn(theater3Dto);

        List<TheaterResponseDto> listOfTheatersByManager = theaterService.getListOfTheatersByManager(authentication);

        assertNotNull(listOfTheatersByManager);
        assertEquals(2,listOfTheatersByManager.size());
        assertTrue(listOfTheatersByManager.contains(theater1Dto));
        assertTrue(listOfTheatersByManager.contains(theater3Dto));
        assertFalse(listOfTheatersByManager.contains(theater2Dto));

        verify(theaterRepository).findByManagerId(managerId);
        verify(userRepository).findByEmail(managerEmail);
        verify(theaterMapper).toDto(theater1);
        verify(theaterMapper).toDto(theater3);
        verify(theaterMapper,never()).toDto(theater2);

    }

    @Test
    void getListOfTheatersByManager_ShouldThrowUserNotFoundException_WhenUserNotExist() {

        String email = "mymail@gmil.com";
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> theaterService.getListOfTheatersByManager(authentication));

        assertEquals("Manager Not Found Exception",userNotFoundException.getMessage());
        verify(authentication).getName();
        verify(userRepository).findByEmail(email);
        verify(theaterRepository,never()).findByManagerId(any());
        verify(theaterMapper,never()).toDto(any());
    }

}