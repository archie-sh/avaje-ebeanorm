package com.avaje.ebeaninternal.server.autotune.service;

import com.avaje.ebean.bean.NodeUsageCollector;
import com.avaje.ebean.bean.ObjectGraphNode;
import com.avaje.ebean.bean.ObjectGraphOrigin;
import com.avaje.ebean.config.AutoTuneConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.server.autotune.AutoTuneCollection;
import com.avaje.ebeaninternal.server.autotune.ProfilingListener;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the collection of object graph usage profiling.
 */
public class ProfileManager implements ProfilingListener {

  private final boolean queryTuningAddVersion;

  /**
   * Converted from a 0-100 int to a double. Effectively a percentage rate at
   * which to collect profiling information.
   */
  private final double profilingRate;

  private final int profilingBase;

  /**
   * Map of the usage and query statistics gathered.
   */
  private final Map<String, ProfileOrigin> profileMap = new ConcurrentHashMap<String, ProfileOrigin>();

  private final Object monitor = new Object();

  private final SpiEbeanServer server;

  public ProfileManager(AutoTuneConfig config, SpiEbeanServer server) {
    this.server = server;
    this.profilingRate = config.getProfilingRate();
    this.profilingBase = config.getProfilingBase();
    this.queryTuningAddVersion = config.isQueryTuningAddVersion();
  }

  @Override
  public boolean isProfileRequest(ObjectGraphNode origin, SpiQuery<?> query) {

    ProfileOrigin profileOrigin = profileMap.get(origin.getOriginQueryPoint().getKey());
    if (profileOrigin == null) {
      profileMap.put(origin.getOriginQueryPoint().getKey(), createProfileOrigin(origin, query));
      return true;
    } else {
      return profileOrigin.isProfile();
    }
  }

  /**
   * Create the profile origin noting the query detail currently being used.
   * <p>
   * For new profiling entries it is useful to compare the profiling against the current
   * query detail that is specified in the code (as the query might already be manually optimised).
   * </p>
   */
  private ProfileOrigin createProfileOrigin(ObjectGraphNode origin, SpiQuery<?> query) {
    ProfileOrigin profileOrigin = new ProfileOrigin(origin.getOriginQueryPoint(), queryTuningAddVersion, profilingBase, profilingRate);
    // set the current query detail (fetch group) so that we can compare against profiling for new entries
    profileOrigin.setOriginalQuery(query.getDetail().toString());
    return profileOrigin;
  }

  /**
   * Gather query execution statistics. This could either be the originating
   * query in which case the parentNode will be null, or a lazy loading query
   * resulting from traversal of the object graph.
   */
  public void collectQueryInfo(ObjectGraphNode node, long beans, long micros) {

    if (node != null) {
      ObjectGraphOrigin origin = node.getOriginQueryPoint();
      if (origin != null) {
        ProfileOrigin stats = getProfileOrigin(origin);
        stats.collectQueryInfo(node, beans, micros);
      }
    }
  }

  /**
   * Collect usage statistics from a node in the object graph.
   * <p>
   * This is sent to use from a EntityBeanIntercept when the finalise method
   * is called on the bean.
   * </p>
   */
  public void collectNodeUsage(NodeUsageCollector usageCollector) {

    ProfileOrigin profileOrigin = getProfileOrigin(usageCollector.getNode().getOriginQueryPoint());
    profileOrigin.collectUsageInfo(usageCollector);
  }

  private ProfileOrigin getProfileOrigin(ObjectGraphOrigin originQueryPoint) {
    synchronized (monitor) {
      ProfileOrigin stats = profileMap.get(originQueryPoint.getKey());
      if (stats == null) {
        stats = new ProfileOrigin(originQueryPoint, queryTuningAddVersion, profilingBase, profilingRate);
        profileMap.put(originQueryPoint.getKey(), stats);
      }
      return stats;
    }
  }

  /**
   * Collect all the profiling information.
   */
  public AutoTuneCollection profilingCollection(boolean reset) {

    AutoTuneCollection req = new AutoTuneCollection();

    for (ProfileOrigin origin : profileMap.values()) {

      BeanDescriptor<?> desc = server.getBeanDescriptorById(origin.getOrigin().getBeanType());
      if (desc != null) {
        origin.profilingCollection(desc, req, reset);
      }
    }

    return req;
  }

}
