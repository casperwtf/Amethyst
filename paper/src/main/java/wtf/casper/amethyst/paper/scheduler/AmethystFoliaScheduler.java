package wtf.casper.amethyst.paper.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import wtf.casper.amethyst.paper.AmethystPaper;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class AmethystFoliaScheduler extends AmethystScheduler {

    private ScheduledTask scheduler;

    @Override
    public AmethystScheduler run(Runnable runnable, Object subject) {
        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().run(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.run();
            });
            return this;
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.entity.Entity" -> {
                Entity entity = (Entity) subject;
                scheduler = entity.getScheduler().run(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                }, () -> {
                    // TODO: ran if entity is removed before ran
                });
            }
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().run(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.run();
                });
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().run(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.run();
                });
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().run(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.run();
                });
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().run(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                });
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runAsync(Runnable runnable, Object subject) {
        scheduler = Bukkit.getAsyncScheduler().runNow(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.run();
        });
        return this;
    }

    @Override
    public AmethystScheduler runLater(Runnable runnable, Object subject, long ticks) {
        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.run();
            }, ticks);
            return this;
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.entity.Entity" -> {
                Entity entity = (Entity) subject;
                scheduler = entity.getScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                }, () -> {
                    // ran if entity is removed before ran
                }, ticks);
            }
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().runDelayed(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.run();
                }, ticks);
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().runDelayed(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.run();
                }, ticks);
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().runDelayed(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.run();
                }, ticks);
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                }, ticks);
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runLaterAsync(Runnable runnable, Object subject, long ticks) {
        scheduler = Bukkit.getAsyncScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.run();
        }, ticks * 50, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimer(Runnable runnable, Object subject, long delay, long ticks) {
        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.run();
            }, delay, ticks);
            return this;
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.entity.Entity" -> {
                Entity entity = (Entity) subject;
                scheduler = entity.getScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                }, () -> {
                    // ran if entity is removed before ran
                }, delay, ticks);
            }
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.run();
                }, delay, ticks);
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.run();
                }, delay, ticks);
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.run();
                }, delay, ticks);
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                }, delay, ticks);
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimerAsync(Runnable runnable, Object subject, long delay, long ticks) {
        scheduler = Bukkit.getAsyncScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.run();
        }, delay * 50, ticks * 50, TimeUnit.MILLISECONDS);

        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        if (repeats == 0) {
            return this;
        }

        AtomicLong counter = new AtomicLong(0);

        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.run();
                if (counter.incrementAndGet() == repeats) {
                    scheduledTask.cancel();
                }
            }, delay, ticks);
            return this;
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.entity.Entity" -> {
                Entity entity = (Entity) subject;
                scheduler = entity.getScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, () -> {
                    // ran if entity is removed before ran
                }, delay, ticks);
            }
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.run();
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.run();
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.run();
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.run();
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, Object subject, long delay, long ticks, long repeats) {

        if (repeats == 0) {
            return this;
        }

        AtomicLong counter = new AtomicLong(0);

        scheduler = Bukkit.getAsyncScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.run();
            if (counter.incrementAndGet() == repeats) {
                scheduledTask.cancel();
            }
        }, delay * 50, ticks * 50, TimeUnit.MILLISECONDS);

        return this;
    }

    @Override
    public void cancel() {
        this.scheduler.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.scheduler.isCancelled();
    }
}
