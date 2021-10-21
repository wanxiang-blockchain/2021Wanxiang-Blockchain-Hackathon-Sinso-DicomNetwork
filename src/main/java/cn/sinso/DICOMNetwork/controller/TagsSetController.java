package cn.sinso.DICOMNetwork.controller;

import cn.sinso.DICOMNetwork.dto.ResultDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/tagsSet/")
public class TagsSetController {
    @GetMapping("/getTags")
    public ResultDTO getTags() {
        List list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("hosCode", "");
//        map.put("tagsData", JSON.toJSONString(tagsOutDTOS));
        map.put("tagsData", list.toString());
        return ResultDTO.ok(map);
//        return ResultDTO.ok(list);

    }
}
