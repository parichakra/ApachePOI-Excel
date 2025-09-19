package com.parichakra.poi_excel.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.parichakra.poi_excel.model.Employee;
import com.parichakra.poi_excel.service.EmployeeService;
import com.parichakra.poi_excel.service.ExcelService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService empService;
    private final ExcelService excelService;

    public EmployeeController(EmployeeService empService, ExcelService excelService) {
        this.empService = empService;
        this.excelService = excelService;
    }

    // ✅ Export Employees
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportEmployees() {
        List<Employee> employees = empService.getAll();
        ByteArrayInputStream in = excelService.exportToExcel(employees);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=employees.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    // ✅ Import Employees
    @PostMapping("/import")
    public ResponseEntity<String> importEmployees(@RequestParam("File") MultipartFile file) {
        List<Employee> employees = excelService.importFromExcel(file);
        empService.saveAll(employees);
        return ResponseEntity.ok("Imported " + employees.size() + " employees successfully!");
    }
}