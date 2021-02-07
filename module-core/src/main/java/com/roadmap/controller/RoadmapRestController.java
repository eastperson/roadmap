package com.roadmap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmap.config.AppProperties;
import com.roadmap.dto.roadmap.NodeDTO;
import com.roadmap.dto.roadmap.StageDTO;
import com.roadmap.dto.roadmap.form.NodeForm;
import com.roadmap.dto.roadmap.form.StageForm;
import com.roadmap.model.Node;
import com.roadmap.model.Roadmap;
import com.roadmap.model.Stage;
import com.roadmap.repository.NodeRepository;
import com.roadmap.repository.RoadmapRepository;
import com.roadmap.repository.StageRepository;
import com.roadmap.service.NodeService;
import com.roadmap.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequestMapping("/roadmap/api/{path}")
@RequiredArgsConstructor
public class RoadmapRestController {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapService roadmapService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;
    private final StageRepository stageRepository;
    private final NodeRepository nodeRepository;
    private final NodeService nodeService;

    @PostMapping("/stage/new")
    public ResponseEntity<StageDTO> registerStage(@RequestBody @Valid StageForm stageForm, Errors errors, @PathVariable String path) throws JsonProcessingException {

        log.info("------------------register new stage---------------------");
        log.info(stageForm);

        if(errors.hasErrors()) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

        Roadmap roadmap = roadmapRepository.findWithAllByPath(path);

        Stage newStage = roadmapService.addNewStage(roadmap,modelMapper.map(stageForm,Stage.class));

        return new ResponseEntity<>(modelMapper.map(newStage, StageDTO.class), HttpStatus.OK);
    }

    @PostMapping("/stage/remove")
    public ResponseEntity<String> removeStage(@PathVariable String path, Long id) {

        log.info("------------------remove stage---------------------");

        Roadmap roadmap = roadmapRepository.findByPath(path);

        roadmapService.removeStage(roadmap,id);

        return new ResponseEntity<>("removed successful", HttpStatus.OK);
    }

    @GetMapping("/stage/get/{ord}")
    public ResponseEntity<StageDTO> getStage(@PathVariable String path,@PathVariable int ord) {

        log.info("------------------get stage---------------------");

        Roadmap roadmap = roadmapRepository.findByPath(path);
        Stage getStage = roadmap.getStageList().stream().filter(stage -> stage.getOrd() == ord).collect(Collectors.toList()).get(0);

        return new ResponseEntity<>(modelMapper.map(getStage,StageDTO.class), HttpStatus.OK);
    }

    @GetMapping("/stage/getList")
    public ResponseEntity<List<StageDTO>> getStageList(@PathVariable String path) {

        log.info("------------------get stage list---------------------");

        Roadmap roadmap = roadmapRepository.findWithAllByPath(path);

        return new ResponseEntity<>(roadmap.getStageList().stream().map(stage -> modelMapper.map(stage,StageDTO.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping("/node/new")
    public ResponseEntity<NodeDTO> registerNode(@RequestBody @Valid NodeForm nodeForm, Errors errors, Long id) throws JsonProcessingException {

        log.info("------------------register new node---------------------");
        log.info(nodeForm);

        if(errors.hasErrors()) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        Node node = null;
        NodeDTO nodeDTO = new NodeDTO();

        if(nodeForm.getParentType().equals("stage")){
            Stage parent = stageRepository.findById(id).orElseThrow();
            node = nodeService.addNewNode(parent,nodeForm);
            nodeDTO.setStageId(parent.getId());
        } else {
            Node parent = nodeRepository.findById(id).orElseThrow();
            node = nodeService.addNewNode(parent,nodeForm);
            nodeDTO.setParentId(parent.getId());
        }

        nodeDTO.setId(node.getId());
        nodeDTO.setNodeType(node.getNodeType());
        nodeDTO.setTitle(node.getTitle());

        return new ResponseEntity<>(nodeDTO, HttpStatus.OK);
    }
}
