package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.ResultSet;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JdbcTemplate jdbcTemplate;

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, DataSource dataSource) {
        this.albumsUpdater = albumsUpdater;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        try {

            if(startAlbumUpdaterTask()) {

                logger.debug("Starting albums update");
                albumsUpdater.update();

                logger.debug("Finished albums update");
            }

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }

    public boolean startAlbumUpdaterTask() {
        return jdbcTemplate.update("update album_scheduler_task set started_at = now() where started_at is NULL OR started_at < date_sub(now(), INTERVAL 2 MINUTE) ") > 0;
    }

}
