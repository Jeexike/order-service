package com.example.orderservice.repository.jdbcRepository;

import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.exception.PartnerNotFoundException;
import com.example.orderservice.repository.PartnerQueries;
import com.example.orderservice.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PartnerRepositoryJdbc implements PartnerRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PartnerEntity> rowMapper = (rs, rowNum) -> {
        PartnerEntity partner = new PartnerEntity();
        partner.setId(rs.getObject("id", UUID.class));
        partner.setName(rs.getString("name"));
        partner.setEmail(rs.getString("email"));
        partner.setCreatedAt(rs.getObject("created_at", Timestamp.class));
        partner.setUpdatedAt(rs.getObject("updated_at", Timestamp.class));
        return partner;
    };

    @Override
    public PartnerEntity getPartnerById(UUID id) {
        try {
            return jdbcTemplate.queryForObject(
                    PartnerQueries.GET_PARTNER_BY_ID,
                    rowMapper,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new PartnerNotFoundException(id);
        }
    }

    @Override
    public List<PartnerEntity> getAllPartners() {
        return jdbcTemplate.query(PartnerQueries.GET_ALL_PARTNERS, rowMapper);
    }

    @Override
    public PartnerEntity createPartner(PartnerEntity partnerEntity) {
        return jdbcTemplate.queryForObject(
                PartnerQueries.CREATE_PARTNER,
                rowMapper,
                partnerEntity.getName(),
                partnerEntity.getEmail()
        );
    }

    @Override
    public PartnerEntity updatePartner(PartnerEntity partnerEntity) {
        if (!existsById(partnerEntity.getId())) {
            throw new PartnerNotFoundException(partnerEntity.getId());
        }

        return jdbcTemplate.queryForObject(
                PartnerQueries.UPDATE_PARTNER,
                rowMapper,
                partnerEntity.getName(),
                partnerEntity.getEmail(),
                partnerEntity.getId()
        );
    }

    @Override
    public void deletePartner(UUID id) {
        if (!existsById(id)) {
            throw new PartnerNotFoundException(id);
        }

        jdbcTemplate.update(PartnerQueries.DELETE_PARTNER, id);
    }

    @Override
    public boolean existsById(UUID id) {
        Boolean exists = jdbcTemplate.queryForObject(
                PartnerQueries.EXISTS_BY_ID,
                Boolean.class,
                id
        );
        return Boolean.TRUE.equals(exists);
    }
}