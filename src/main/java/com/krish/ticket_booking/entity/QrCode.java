package com.krish.ticket_booking.entity;

import com.krish.ticket_booking.entity.enums.QrCodeStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="Qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCode {
	@Id
	@Column(name="id",updatable = false,nullable = false)
	private UUID id;

	@Column(name = "status",nullable = false)
	@Enumerated(EnumType.STRING)
	private QrCodeStatusEnum status;

	@Column(name = "value",columnDefinition = "TEXT", nullable = false)
	private  String value;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id")
	private Ticket ticket;

	@CreatedDate
	@Column(name="created_at",updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name="updated_at")
	private LocalDateTime updatedAt;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		QrCode qrCode = (QrCode) o;
		return Objects.equals(id, qrCode.id) && status == qrCode.status && Objects.equals(value, qrCode.value) && Objects.equals(createdAt, qrCode.createdAt) && Objects.equals(
				updatedAt, qrCode.updatedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, status, value, createdAt, updatedAt);
	}
}
