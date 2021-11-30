package net.codejava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.codejava.Machine;
import net.codejava.Main;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path="/data")
public class MachineController {
    @GetMapping
    public String getAttributes(){
        return Machine.getInstance().toString();
    }

}


/*
@RestController
@RequestMapping(path="/data")
public class MachineController {
    @Autowired
    Main main;
    @GetMapping
    public String getAttributes(){
        return Main.getStuff();
    }

}

 */