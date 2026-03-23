package com.socialsync.socialsyncbackend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.socialsync.socialsyncbackend.dto.SupportTicketRequest;
import com.socialsync.socialsyncbackend.dto.SupportTicketResponse;
import com.socialsync.socialsyncbackend.entity.SupportTicket;
import com.socialsync.socialsyncbackend.entity.TicketStatus;
import com.socialsync.socialsyncbackend.entity.User;

@ExtendWith(MockitoExtension.class)
class SupportServiceTest {

    @Mock
    private com.socialsync.socialsyncbackend.repositories.SupportTicketRepository ticketRepository;

    @Mock
    private com.socialsync.socialsyncbackend.repositories.UserRepository userRepository;

    @InjectMocks
    private SupportService supportService;

    @Test
    void createTicket_shouldPersistAndMapResponse() {
        Long userId = 7L;
        SupportTicketRequest request = new SupportTicketRequest("Issue", "Something is broken");
        User user = User.builder().id(userId).email("u@example.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket t = invocation.getArgument(0);
            t.setId(101L);
            t.setCreatedAt(LocalDateTime.of(2026, 3, 23, 12, 0));
            return t;
        });

        SupportTicketResponse response = supportService.createTicket(userId, request);

        ArgumentCaptor<SupportTicket> ticketCaptor = ArgumentCaptor.forClass(SupportTicket.class);
        verify(ticketRepository).save(ticketCaptor.capture());
        SupportTicket saved = ticketCaptor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals("Issue", saved.getSubject());
        assertEquals("Something is broken", saved.getDescription());
        assertEquals(TicketStatus.OPEN, saved.getStatus());

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals("Issue", response.getSubject());
        assertEquals("Something is broken", response.getDescription());
        assertEquals("OPEN", response.getStatus());
    }

    @Test
    void createTicket_shouldThrowWhenUserMissing() {
        Long userId = 99L;
        SupportTicketRequest request = new SupportTicketRequest("Issue", "Description");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> supportService.createTicket(userId, request));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserTickets_shouldMapTicketListToResponses() {
        Long userId = 7L;
        SupportTicket t1 = SupportTicket.builder()
                .id(1L)
                .subject("S1")
                .description("D1")
                .status(TicketStatus.OPEN)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();
        SupportTicket t2 = SupportTicket.builder()
                .id(2L)
                .subject("S2")
                .description("D2")
                .status(TicketStatus.RESOLVED)
                .createdAt(LocalDateTime.of(2026, 1, 2, 10, 0))
                .build();

        when(ticketRepository.findByUserId(userId)).thenReturn(List.of(t1, t2));

        List<SupportTicketResponse> responses = supportService.getUserTickets(userId);

        assertEquals(2, responses.size());
        assertEquals("S1", responses.get(0).getSubject());
        assertEquals("OPEN", responses.get(0).getStatus());
        assertEquals("S2", responses.get(1).getSubject());
        assertEquals("RESOLVED", responses.get(1).getStatus());
    }

    @Test
    void getFAQs_shouldReturnConfiguredFaqList() {
        List<String> faqs = supportService.getFAQs();

        assertEquals(5, faqs.size());
        assertEquals("How do I connect a social media account?", faqs.get(0));
    }
}
