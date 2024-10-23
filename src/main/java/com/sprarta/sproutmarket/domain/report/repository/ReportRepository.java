package com.sprarta.sproutmarket.domain.report.repository;

import com.sprarta.sproutmarket.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByItemId(Long id);

}
