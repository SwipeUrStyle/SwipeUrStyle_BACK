package com.swipeurstyle.jwt.backend.service;

import com.swipeurstyle.jwt.backend.entity.User;
import com.swipeurstyle.jwt.backend.exception.UserNotFoundException;
import com.swipeurstyle.jwt.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterNewUser() {
        // Crear un usuario de ejemplo
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("testpass");
        user.setUsername("testuser");
        user.setName("Test User");

        // Simular el comportamiento del repositorio
        when(userRepositoryMock.save(user)).thenReturn(user);

        // Llamar al método del servicio que quieres probar
        User registeredUser = userService.registerNewUSer(user);

        // Verificar el resultado
        assertNotNull(registeredUser);
        assertEquals("test@gmail.com", registeredUser.getEmail());
    }

    @Test
    public void testUpdateUser() throws UserNotFoundException {
        // Crear un usuario de ejemplo
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("testpass");
        user.setUsername("testuser");
        user.setName("Test User");

        // Simular el comportamiento del repositorio
        when(userRepositoryMock.findById("test@gmail.com")).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(user)).thenReturn(user);

        // Llamar al método del servicio que quieres probar
        User updatedUser = userService.updateUser(user);

        // Verificar el resultado
        assertNotNull(updatedUser);
        assertEquals("test@gmail.com", updatedUser.getEmail());
    }
}

