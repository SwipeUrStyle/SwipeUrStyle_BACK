package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.repository.OutfitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OutfitServiceTest {

    @Mock
    private OutfitRepository outfitRepositoryMock;

    @Mock
    private GarmentService garmentServiceMock;

    @InjectMocks
    private OutfitService outfitService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
    }


}