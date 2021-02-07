package com.roadmap.service;

import com.roadmap.dto.roadmap.form.NodeForm;
import com.roadmap.model.Node;
import com.roadmap.model.Stage;
import com.roadmap.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Transactional
@Service
@Log4j2
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final ModelMapper modelMaper;


    public Node addNewNode(Stage stage, NodeForm nodeForm){
        Node newNode = modelMaper.map(nodeForm,Node.class);
        newNode.setStage(stage);
        stage.getNodeList().add(newNode);
        return nodeRepository.save(newNode);
    }

    public Node addNewNode(Node node, NodeForm nodeForm){
        Node newNode = modelMaper.map(nodeForm,Node.class);
        newNode.setParent(node);
        node.getChilds().add(newNode);
        return nodeRepository.save(newNode);
    }

}
