package com.model;

import java.util.ArrayList;
import java.util.List;

public class SkillsRepository {


    private static Skill skill1 = new Skill(
            "68428a4a-b992-42ac-afe2-2a3e11c54bef",
            "teamwork"
    );

    private static Skill skill2 = new Skill(
            "fdcd63fb-ef7a-4d73-86b4-2571bb15f3a",
            "creativity"
    );

    private static Skill skill3 = new Skill(
            "3af71f10-d992-457a-847e-b3b6f1b14c5e",
            "communication"
    );

    private static Skill skill4 = new Skill(
            "67693ec6-263a-4931-a895-2e3fa11f093a",
            "leadership"
    );


    private static List<Skill> skills = new ArrayList<>();

    static {
        skills.add(skill1);
        skills.add(skill2);
        skills.add(skill3);
        skills.add(skill4);
    }

    public static List<Skill> getSkillsData() {
        return skills;
    }

}