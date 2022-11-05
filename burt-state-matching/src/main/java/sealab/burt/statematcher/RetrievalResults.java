package sealab.burt.statematcher;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RetrievalResults {
    
    List<String> states;
    Integer numberOfStates;
}
