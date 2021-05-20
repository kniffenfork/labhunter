package ru.lab.hunter.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "hhClient", url = "${headhunter.api.url}")
public interface HeadHunterClient {

    @RequestMapping(path = "/vacancies?text=лаборант", method = RequestMethod.GET)
    ResponseEntity<?> getLabTechnicianVacancies();
}
