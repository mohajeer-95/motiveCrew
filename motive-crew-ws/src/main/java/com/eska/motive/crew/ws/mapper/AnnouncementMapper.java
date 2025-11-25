package com.eska.motive.crew.ws.mapper;

import com.eska.motive.crew.ws.dto.response.AnnouncementDTO;
import com.eska.motive.crew.ws.entity.Announcement;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public AnnouncementDTO toDto(Announcement announcement) {
        if (announcement == null) {
            return null;
        }

        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .message(announcement.getMessage())
                .imageUrl(announcement.getImageUrl())
                .audience(announcement.getAudience())
                .teamId(announcement.getTeam() != null ? announcement.getTeam().getId() : null)
                .teamName(announcement.getTeam() != null ? announcement.getTeam().getName() : null)
                .isActive(announcement.getIsActive())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .expiresAt(announcement.getExpiresAt())
                .build();
    }
}


