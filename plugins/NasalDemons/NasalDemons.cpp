// PluginNasalDemons.cpp
// Gianluca Elia (elgiano@gmail.com)

#include "NasalDemons.hpp"
#include "SC_PlugIn.hpp"
#include <iomanip>
#include <iostream>
#include <sstream>
static InterfaceTable *ft;

namespace NasalDemons {

NasalDemons::NasalDemons() {
  mCalcFunc = make_calc_function<NasalDemons, &NasalDemons::next>();
  readStartAddr();
  mPtr = reinterpret_cast<float *>(mAddrStart);
  mPos = mFracPos = 0;
  mRFloatSize = 1. / sizeof(float);
  next(1);
}

void NasalDemons::readStartAddr() {
  std::stringstream tmp;
  tmp << "0x";
  for (int i = 0; i < 4; i++) {
    tmp << std::hex << std::setw(4) << std::setfill('0')
        << static_cast<int>(in0(i));
  }
  tmp >> mAddrStart;
}

void NasalDemons::next(int nSamples) {
  mSize = static_cast<int> (in0(4) * mRFloatSize);
  int loop = in0(5);
  if (loop && !mLoop)
    mDone = false;
  mLoop = loop;

  mRate = in0(6);
  mPost = in0(7);

  ClearUnitOutputs((Unit *)this, nSamples);
  if (mDone) return;

  if (mPost) Print("%p\n", mPtr);

  for (int i = 0; i < nSamples; ++i) {
    for (int j = 0; j < mNumOutputs; j++) {
      out(j)[i] = mPtr[mPos];
      if (!advancePtr())
        return;
    }
  }
}

inline bool NasalDemons::advancePtr() {
  mFracPos += mRate * sizeof(float);
  mPos = static_cast<int>(mFracPos * mRFloatSize);
  if (mPos >= 0 && mPos < mSize) {
    return true;
  } else if (mLoop) {
    if(mPos > 0){
      mPos = mFracPos = 0;
    }else{
      mPos = mSize - 1;
      mFracPos = mSize - sizeof(float);
    }
    return true;
  } else {
    mPos = mFracPos = 0;
    mDone = true;
    return false;
  }
}

} // namespace NasalDemons

PluginLoad(NasalDemonsUGens) {
  // Plugin magic
  ft = inTable;
  registerUnit<NasalDemons::NasalDemons>(ft, "NasalDemons");
}
