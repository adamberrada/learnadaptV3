package com.anouar.elearning.notification.dto;

import com.anouar.elearning.notification.entity.ChannelType;
import jakarta.validation.constraints.NotNull;

public record NotificationSettingRequest(@NotNull Boolean enableEmail, @NotNull Boolean enableInApp, @NotNull ChannelType channelType) {}
