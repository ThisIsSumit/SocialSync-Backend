package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.dto.*;
import com.socialsync.socialsyncbackend.entity.*;
import com.socialsync.socialsyncbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public SupportTicketResponse createTicket(Long userId, SupportTicketRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(TicketStatus.OPEN)
                .build();

        ticket = ticketRepository.save(ticket);

        return mapToResponse(ticket);
    }

    public List<SupportTicketResponse> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<String> getFAQs() {
        return List.of(
                "How do I connect a social media account?",
                "How do I schedule a post?",
                "What analytics are available?",
                "How do I change my password?",
                "What platforms are supported?"
        );
    }

    private SupportTicketResponse mapToResponse(SupportTicket ticket) {
        return SupportTicketResponse.builder()
                .id(ticket.getId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus().name())
                .createdAt(ticket.getCreatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}
