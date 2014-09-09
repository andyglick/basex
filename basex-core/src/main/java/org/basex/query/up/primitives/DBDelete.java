package org.basex.query.up.primitives;

import org.basex.core.cmd.*;
import org.basex.data.*;
import org.basex.query.func.*;
import org.basex.util.*;
import org.basex.util.list.*;

/**
 * Update primitive for the {@link Function#_DB_DELETE} function.
 *
 * @author BaseX Team 2005-14, BSD License
 * @author Christian Gruen
 */
public final class DBDelete extends DBUpdate {
  /** Resources to be deleted. */
  private final StringList paths = new StringList(1);
  /** Number of keys. */
  private int size;

  /**
   * Constructor.
   * @param data data
   * @param path target path
   * @param info input info
   */
  public DBDelete(final Data data, final String path, final InputInfo info) {
    super(UpdateType.DBDELETE, data, info);
    paths.add(path);
    size = data.resources.binariesIn(path).size();
  }

  @Override
  public void merge(final Update up) {
    for(final String path : ((DBDelete) up).paths) paths.add(path);
    size += up.size();
  }

  @Override
  public void apply() {
    for(final String path : paths) Delete.delete(data,  path);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public void prepare(final MemData tmp) { }
}
