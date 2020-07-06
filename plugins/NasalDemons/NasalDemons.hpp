// PluginNasalDemons.hpp
// Gianluca Elia (elgiano@gmail.com)

#pragma once

#include "SC_PlugIn.hpp"

namespace NasalDemons {

class NasalDemons : public SCUnit {
public:
  NasalDemons();

  // Destructor
  // ~NasalDemons();

private:
  // Calc function
  void next(int nSamples);
  void readStartAddr();
  bool advancePtr();

  // Member variables
  float *mPtr;
  unsigned long mAddrStart;
  int mSize, mPos;
  int mLoop, mPost;
  float mFracPos, mRate;

  double mRFloatSize;
};

} // namespace NasalDemons
