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
import java.util.function.Consumer;

public class AmethystFoliaScheduler extends AmethystScheduler {


    private ScheduledTask scheduler;

    @Override
    public AmethystScheduler run(Consumer<AmethystScheduler> runnable, Object subject) {
        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().run(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
            });
            return this;
        }

        if (subject.getClass().isAssignableFrom(Entity.class)) {
            Entity entity = (Entity) subject;
            scheduler = entity.getScheduler().run(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
            }, () -> {

            });
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().run(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.accept(this);
                });
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().run(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.accept(this);
                });
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().run(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.accept(this);
                });
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().run(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.accept(this);
                });
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runAsync(Consumer<AmethystScheduler> runnable, Object subject) {
        scheduler = Bukkit.getAsyncScheduler().runNow(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.accept(this);
        });
        return this;
    }

    @Override
    public AmethystScheduler runLater(Consumer<AmethystScheduler> runnable, Object subject, long ticks) {
        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
            }, ticks);
            return this;
        }

        if (subject.getClass().isAssignableFrom(Entity.class)) {
            Entity entity = (Entity) subject;
            scheduler = entity.getScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
            }, () -> {
                // ran if entity is removed before ran
            }, ticks);
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().runDelayed(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.accept(this);
                }, ticks);
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().runDelayed(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.accept(this);
                }, ticks);
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().runDelayed(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.accept(this);
                }, ticks);
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.accept(this);
                }, ticks);
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runLaterAsync(Consumer<AmethystScheduler> runnable, Object subject, long ticks) {
        scheduler = Bukkit.getAsyncScheduler().runDelayed(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.accept(this);
        }, ticks * 50, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks) {
        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
            }, delay, ticks);
            return this;
        }

        if (subject.getClass().isAssignableFrom(Entity.class)) {
            Entity entity = (Entity) subject;
            scheduler = entity.getScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
            }, () -> {
                // ran if entity is removed before ran
            }, delay, ticks);
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.accept(this);
                }, delay, ticks);
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.accept(this);
                }, delay, ticks);
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.accept(this);
                }, delay, ticks);
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.accept(this);
                }, delay, ticks);
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks) {
        scheduler = Bukkit.getAsyncScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.accept(this);
        }, delay * 50, ticks * 50, TimeUnit.MILLISECONDS);

        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats) {
        if (repeats == 0) {
            return this;
        }

        AtomicLong counter = new AtomicLong(0);

        if (subject == null) {
            scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
                if (counter.incrementAndGet() == repeats) {
                    scheduledTask.cancel();
                }
            }, delay, ticks);
            return this;
        }

        if (subject.getClass().isAssignableFrom(Entity.class)) {
            Entity entity = (Entity) subject;
            scheduler = entity.getScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                runnable.accept(this);
                if (counter.incrementAndGet() == repeats) {
                    scheduledTask.cancel();
                }
            }, () -> {
                // ran if entity is removed before ran
            }, delay, ticks);
        }

        switch (subject.getClass().getName()) {
            case "org.bukkit.Chunk" -> {
                Chunk chunk = (Chunk) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), chunk.getWorld(), chunk.getX(), chunk.getZ(), scheduledTask -> {
                    runnable.accept(this);
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
            case "org.bukkit.Location" -> {
                Location location = (Location) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), location, scheduledTask -> {
                    runnable.accept(this);
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
            case "org.bukkit.block.Block" -> {
                Block block = (Block) subject;
                scheduler = Bukkit.getRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), block.getLocation(), scheduledTask -> {
                    runnable.accept(this);
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
            default -> {
                scheduler = Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
                    runnable.accept(this);
                    if (counter.incrementAndGet() == repeats) {
                        scheduledTask.cancel();
                    }
                }, delay, ticks);
            }
        }

        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats) {

        if (repeats == 0) {
            return this;
        }

        AtomicLong counter = new AtomicLong(0);

        scheduler = Bukkit.getAsyncScheduler().runAtFixedRate(AmethystPaper.getInstance(), scheduledTask -> {
            runnable.accept(this);
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
