package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Story;

public interface StoryRepository extends JpaRepository<Story, Long>{
    Story findStoryByStoryId(Long storyId);
}
